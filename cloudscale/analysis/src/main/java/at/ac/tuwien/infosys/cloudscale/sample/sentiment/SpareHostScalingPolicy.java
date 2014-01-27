package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import at.ac.tuwien.infosys.cloudscale.policy.IScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.CloudScaleClient;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;
import at.ac.tuwien.infosys.cloudscale.vm.IVirtualHost;

import java.util.UUID;

/**
 * User: brb
 * Date: 11/27/13
 * Time: 3:33 PM
 */
public class SpareHostScalingPolicy implements IScalingPolicy {

    //Min. number of objects (inclusive) for host to be selected quickly.
    private static int MIN_THRESHOLD = 1;

    //Max. number of objects (inclusive) for host with lowest load to use the spare.
    private static int MAX_THRESHOLD = 4;

    //The load the spare has to reach to trigger scaling up.
    private static int SCALING_THRESHOLD = (int) Math.ceil(0.5 * MAX_THRESHOLD);
    
    private static int MIN_NUMBER_INSTANCES = 2;

    private UUID spareHost = null;
    private boolean startingInstance = false;


    @Override
    public synchronized IHost selectHost(ClientCloudObject clientCloudObject, IHostPool iHostPool) {

        //choose new spare host if necessary
        if(spareHost == null || !iHostPool.getHostById(spareHost).isOnline()) {
            System.out.println("no spare host found or offline.");
            IHost spare = selectBestHost(iHostPool, null, false);
            if (spare != null && spare.getId() != null) {
                spareHost = spare.getId();
                System.out.println("new spare host selected: " + spareHost.toString());
            }
            else {
                System.out.println("no suitable spare host found. waiting for next upscale.");
            }
        }


        //try to find first host with low object count
        IHost selected = selectBestHost(iHostPool, spareHost, true);

        if(selected == null || selected.getCloudObjectsCount() >= MAX_THRESHOLD) {

            // use spare to handle peak load while we start up a new host (if necessary).
            System.out.println("Max threshold system load reached.");

            if(spareHost != null)
            {
                IHost spare = iHostPool.getHostById(spareHost);

                if (selected == null || spare.getCloudObjectsCount() < selected.getCloudObjectsCount()) {
                    System.out.println("Selecting spare host to handle peak.");
                    selected = spare;
                }
                else {
                    System.out.println("Spare has higher load than lowest regular host.");
                }


                if(!startingInstance && spare.getCloudObjectsCount() >= SCALING_THRESHOLD) {
                    System.out.println("scaling threshold reached, scaling up.");
                    startNewHost(iHostPool);
                }

            } else if(selected == null) {
                System.out.println("Edge case: no hosts found.");
                selected = startNewHost(iHostPool);
            }
        }
        return selected;
    }



    private IHost selectBestHost(IHostPool iHostPool, UUID ignore, boolean useThreshold) {

        int minObjectCount = Integer.MAX_VALUE;
        IHost candidate = null;
	System.out.println("select best host from a total of "  + iHostPool.getHostsCount());
        for(IHost host : iHostPool.getHosts())
        {
	    // System.out.println("checking host: " + host.getId());
            // ignore the spare
            if(host.getId() != null && host.getId().equals(ignore))
                continue;

            //ignore offline hosts
           // if(!host.isOnline())
           //     continue;

            int count = host.getCloudObjectsCount();

            // quick selection to save time
            if(useThreshold && count <= MIN_THRESHOLD)
                return host;

            if (count >= minObjectCount)
                continue;

            minObjectCount = count;
            candidate = host;
        }
        return candidate;
    }

    private synchronized IHost startNewHost(IHostPool iHostPool) {

        System.out.println("Firing up new instance.");
        startingInstance = true;
        return iHostPool.startNewHostAsync(new IHostPool.IHostStartedCallback() {
            @Override
            public void startupFinished(IHost iHost) {
                System.out.println("New instance started.");
                startingInstance = false;
            }
        });
    }

    @Override
    public synchronized boolean scaleDown(IHost iHost, IHostPool iHostPool) {

        System.out.println("-------------------------------------------------------------");
	    System.out.println("Checking whether to scale down host "+iHost.getId().toString() + " started at " + (iHost.getStartupTime() != null ? iHost.getStartupTime().toString() : "null"));
	    boolean isStatic = false;
	    
	    if(iHost instanceof IVirtualHost)
	    {
	    	System.out.println("Check if Host is static");
	    	isStatic = ((IVirtualHost)iHost).isStaticHost();
	    }
	    
	    
	    if(isStatic) {
	    	System.out.println("Not scaling down, host is static");
            return false;
	    }
	    
        // keep at least 2 instances + spare.
        if(iHostPool.getHostsCount() <= MIN_NUMBER_INSTANCES) {
            System.out.println("Not scaling down, minimum number of instances reached");
            return false;
        }

        //spare should be kept
        if(iHost.getId().equals(spareHost)) {
            System.out.println("Not scaling down the spare host.");
            return false;
        }


        //do not scale down if spare host is not idle
        if(spareHost != null)
        {
            IHost spare = iHostPool.getHostById(spareHost);
            int c = spare.getCloudObjectsCount();
            if( spare != null && c > 0)
            {
                System.out.println("Not scaling down, spare host is in use (" + c + ")");
                return false;
            }
        }

        //remove the host if it is not used
        if(iHost.isOnline() && iHost.getCloudObjectsCount() == 0) {
            System.out.println("Host is unused. scaling down.");
            return true;
        }
        System.out.println("Not scaling down. Host is offline or in use.");
        return false;

    }
}
