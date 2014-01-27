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

import at.ac.tuwien.infosys.cloudscale.policy.IScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;
import at.ac.tuwien.infosys.cloudscale.vm.IVirtualHostPool;

public class FixedNumberScalingPolicy implements IScalingPolicy {

	private Object lock = new Object();

	@Override
	public IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool)
	{

		System.out.println("Starting to select host from " + hostPool.getHostsCount() + " available hosts");
		
		// lock the policy so that we do not concurrently fire up a bunch of hosts
		synchronized (lock) {
			
			System.out.println("-------------------------------------------------------------");

			IHost selected = null;
			
			for (IHost host : hostPool.getHosts()) {
				int hostedObjects = host.getCloudObjectsCount();
				System.out.println(String.format(
					"Host %s (%s) has current %d active objects",
					host.getId().toString(), host.getIpAddress(), hostedObjects
				));
				if(hostedObjects < 5) {
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
			
			if(selected != null)
				return selected;
			
		}
		
		// no suitable host found, start a new one
		// (and start a reserve instance)
		// hostPool.startNewHostAsync();
		IHost newHost = hostPool.startNewHost();
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
