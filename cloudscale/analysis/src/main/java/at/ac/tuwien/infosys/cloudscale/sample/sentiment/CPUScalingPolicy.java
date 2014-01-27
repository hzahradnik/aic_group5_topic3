/*
   Copyright 2013 Philipp Leitner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.util.UUID;

import sun.util.logging.resources.logging;
import at.ac.tuwien.infosys.cloudscale.messaging.objects.monitoring.CPUEvent;
import at.ac.tuwien.infosys.cloudscale.monitoring.CPUUsage;
import at.ac.tuwien.infosys.cloudscale.monitoring.EventCorrelationEngine;
import at.ac.tuwien.infosys.cloudscale.monitoring.IMetricsDatabase;
import at.ac.tuwien.infosys.cloudscale.policy.IScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;
import at.ac.tuwien.infosys.cloudscale.vm.IVirtualHostPool;

public class CPUScalingPolicy implements IScalingPolicy {

	private Object lock = new Object();

	@Override
	public IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool)
	{

		System.out.println("Starting to select host from " + hostPool.getHostsCount() + " available hosts");
		
		// lock the policy (we do not want to scale up and down at the same time)
		synchronized (lock) {
			
			System.out.println("-------------------------------------------------------------");

			IHost selected = null;
			
			// for each host, check the current CPU load
			// (unused hosts we are always using)
			for (IHost host : hostPool.getHosts()) {
				System.out.println(String.format("Look at host %s (%s)",
						host.getId().toString(), host.getIpAddress()));
				
				CPUUsage cpu = host.getCurrentCPULoad();
				EventCorrelationEngine ece = EventCorrelationEngine.getInstance();
				IMetricsDatabase db = null;
				if (ece != null)
				{
					db = ece.getMetricsDatabase();
				}
				Object val = null;
				if(db != null)
				{
					val = db.getLastValue("CPULoad_"+host.getId().toString());
				}
				CPUEvent cpuEvent = null;
				if(val != null)
				{
					cpuEvent = (CPUEvent) val;
				}
				
				System.out.println(String.format("CPUUsage is%s null", cpu == null ? "" : " not"));
				System.out.println(String.format("EventCorrelationEngine is%s null", ece == null ? "" : " not"));
				System.out.println(String.format("IMetricsDatabase is%s null", db == null ? "" : " not"));
				System.out.println(String.format("CPUEvent is%s null", cpuEvent == null ? "" : " not"));
				System.out.println(String.format(
					"Host %s (%s) has reported current CPU load %f",
					host.getId().toString(), host.getIpAddress(), cpu.getCpuLoad()
				));
				if((cpu.getCpuLoad() != -1 && cpu.getCpuLoad() < 0.75) || host.getCloudObjectsCount() == 0) {
					selected = host;
				}
			}
			System.out.println("-------------------------------------------------------------");
			if(selected == null)
				System.out.println("Found no suitable host, scaling up");
			else
            {
				System.out.println("Using host "+selected.getId().toString());
                return selected;
            }
			System.out.println("-------------------------------------------------------------");

		}
			
		// no suitable host found, start a new one (and register monitoring for the host)
		// (and start a reserve instance)
		IHost newHost = hostPool.startNewHost();
		hostPool.startNewHostAsync();
		return newHost;
	}

	@Override
	public boolean scaleDown(IHost host, IHostPool hostPool) {

		// we scale down iff
		// - it is online
		// - the host is currently unused
		// - this is none of our static instances, which we never tear down
		// - there is at least one other unused host

		boolean result = true;
		
		synchronized (lock) {

			System.out.println("-------------------------------------------------------------");
			System.out.println("Checking whether to scale down host "+host.getId().toString());
			
			if(!host.isOnline()) {
				result = false;
				System.out.println("Not scaling down. Host is offline");
			}

			if(host.getCloudObjectsCount() > 0) {
				result = false;
				System.out.println("Not scaling down. Host is in use");
			}

			if(!otherUnusedHost(hostPool, host.getId())) {
				result = false;
				System.out.println("Not scaling down. Host is the last unused host");
			}
		}
		if(result) {
			System.out.println("Scaling down host "+host.getId().toString());
		}
		return result;
	}

	private boolean otherUnusedHost(IHostPool hostPool, UUID id) {

		for(IHost host : hostPool.getHosts()) {
			if(!host.getId().equals(id) && host.getCloudObjectsCount() == 0)
				return true;
		}

		return false;

	}
}

