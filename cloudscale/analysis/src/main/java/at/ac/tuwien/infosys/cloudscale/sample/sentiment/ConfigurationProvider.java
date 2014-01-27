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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleConfigurationProvider;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfiguration;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfigurationBuilder;
import at.ac.tuwien.infosys.cloudscale.messaging.MessageQueueConfiguration;
import at.ac.tuwien.infosys.cloudscale.vm.ec2.EC2CloudPlatformConfiguration;

public class ConfigurationProvider {
	
	@CloudScaleConfigurationProvider
	public static CloudScaleConfiguration getConfiguration()
			throws FileNotFoundException, IOException
	{
		Properties properties = new Properties();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream("analysis.properties"));
		properties.load(stream);
		stream.close();
		
		if(properties.isEmpty()) {
			throw new IllegalArgumentException("Einstellungsdatei ist leer");
		}
		
		EC2CloudPlatformConfiguration platformConfig = new EC2CloudPlatformConfiguration();
		platformConfig.setAwsConfigFile("aws.config");
		platformConfig.setAwsEndpoint(properties.getProperty("awsEndpoint"));
		platformConfig.setInstanceType(properties.getProperty("instanceType"));
		platformConfig.setSshKey(properties.getProperty("sshKey"));
		//platformConfig.setMqImageName("ami-f12eb2cb");
		MessageQueueConfiguration mqConfig = new MessageQueueConfiguration();
		mqConfig.setServerAddress(properties.getProperty("mqServer"));
		platformConfig.setMessageQueueConfiguration(mqConfig);
		
		
					
		// this method delivers the configuration for cloudscale
		CloudScaleConfiguration cfg = CloudScaleConfigurationBuilder
				// enable local configuration for testing ...
				.createLocalConfigurationBuilder()
				.withMQServerHostname(properties.getProperty("mqServer"))
				.with(platformConfig)
				// or Openstack configuration to actually deploy to the cloud
//				.createOpenStackConfigurationBuilder("openstack.props",
//						"128.130.172.197")
				.withGlobalLoggingLevel(Level.SEVERE)
// 				.with(new SentimentScalingPolicy())
//				.with(new CPUScalingPolicy())
//				.with(new FixedNumberScalingPolicy())
//				.with(new MyScalingPolicy())
				.with(new SpareHostScalingPolicy())
//				.withMonitoring(true)
// 				.withMonitoringEvents(ClassificationDurationEvent.class)
				.build();
		
		
		cfg.server().setStaticHostDiscoveryInterval(1000);
		
		// this governs how often we run the scaling-down check for each thread
		// (check every 5 minutes)
		cfg.common().setScaleDownIntervalInSec(30);
		
		// as we will get some classloading exceptions from Twitter4J (expected), we disable some loggers

		cfg.server().logging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.RemoteClassLoader", Level.OFF);
		cfg.server().logging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.fileCollectors.FileBasedFileCollector", Level.OFF);
		cfg.common().clientLogging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.RemoteClassProvider", Level.OFF);
		cfg.common().clientLogging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.fileCollectors.FileBasedFileCollector", Level.OFF);
		
		return cfg;
		
	}
	
}	
