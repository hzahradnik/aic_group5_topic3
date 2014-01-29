aic_group5_topic3
=================

Advanced Internet Computing - Gruppe 5 - Topic 3
------------------------------------------------

### Amazon Web Services
If you want to deploy our solution in AWS, you have to create at minimum one instance for the ActiveMQ server to run, the Web GUI and the Sentiment Analysis. We used Ubuntu 13.04 64-bit as OS for all virtual machines.

You can also can install each of them on a single instance, but then you have to make sure, that the instances are able to communicate to each other on the needed ports.

We tested the solution with two instances: One for the stand-alone ActiveMQ, the other bigger one (m1.small) for the Web GUI and the Sentiment Analysis.

Additionally you have to create a image for the single instances, so Cloudscale is able to create new instances.

#### Security Groups
To allow the instances to communicate to each other, you have to assign security groups to each instance. In a security group you can define inbound and outbound rules for the network traffic.

Because cloudscale assigns the _default_ security group to allocated instances, you should use this security group also for the other instances and set the following rules to it:

##### Inbound
| Rule type | Port (Service) | Source |
|---------|----------------|--------|
| ALL | ALL | _id security group_ |
| ICMP | ALL | 0.0.0.0/0 |
| TCP | 22 (SSH) | 0.0.0.0/0 |
| TCP | 80 (HTTP) | 0.0.0.0/0 |
| TCP | 443 (HTTPS) | 0.0.0.0/0 |
| TCP | 8080 (HTTP*) | 0.0.0.0/0 |
| TCP | 61616 | 0.0.0.0/0 |

##### Outbound
| Rule type | Port (Service) | Source |
|---------|----------------|--------|
| ALL | ALL | 0.0.0.0/0 |
| ALL | ALL | _id security group_ |

#### Elastic IPs
When an instance was shutdown and it gets restarted, AWS doesn't always allocate the same IP for this instance. Because the IP of the ActiveMQ is hardwritten in the image for Cloudscale, you have to assign an Elastic IP to it. Same applies to the instance of the Sentiment Analysis, so assign an Elatic IP too.

Athough you don't need it to run the project, we recommend to also get an Elastic IP for the Web GUI, if you plan to run it on a own instance.

#### ActiveMQ
Set up a instance with ActiveMQ running according to the guide on: http://activemq.apache.org/getting-started.html

The message queue is using port 61616, which has to be open on this instance, the sentiment analysis instance and the instances Cloudscale is creating.

#### Sentiment Analysis
Now create a new instance and install _maven_ and _open-jdk_ on it.

Connect to the instance and copy the folder _analysis_ (inside the folder _cloudscale_) to the machine. Before you can start the application, you have to edit the file _aws.config_ located in `src/main/resources` (if not create it) to your settings of AWS.

    accessKey = YOURACCESSKEY
    secretKey = yoursecretkey
    mq.address = ec2-54-206-35-230.ap-southeast-2.compute.amazonaws.com (replace with IP or DNS of your ActiveMQ)

Furthermore you have to edit the file _analysis.properties_ located in `src/main/resources` (if not create it).

    awsEndpoint=AWSENDPOINT
    instanceType=INSTANCETYPE (e.g. t1.micro)
    sshKey=SSHKEY_NAME
    mqServer=ec2-54-206-41-183.ap-southeast-2.compute.amazonaws.com (replace with IP or DNS of your ActiveMQ)

Now you can run the command `mvn clean compile exec:exec`

#### Web GUI
Create a new instance and install the play framework from www.playframework.com/download

When you're done, copy the folder _awsgui_ (inside the folder _cloudscale_) to this instance and edit the file `conf/application.conf`.

    app.url="http://ec2-54-206-49-26.ap-southeast-2.compute.amazonaws.com:8080/api/sentiment" (replace with IP or DNS of your Sentiment Analysis, but don't change the port and path of the resource)

Now you can start the application with `sudo /relative/path/to/play "start -DapplyEvolutions.default=true -Dhttp.port=80"`

#### Cloudscale Image
At last you have to create the image, which will be used to start up new instances. Please follow the instruction without the last step on https://code.google.com/p/cloudscale/wiki/BuildingServerImages

Before you create the image connect to the machine and create a file `/opt/cloudscale/CloudScaleMessageQueueConnection.cfg`

    ec2-54-206-35-230.ap-southeast-2.compute.amazonaws.com:61616

Afterwards you can create the image and give it the name _CloudScale_v0.2.0_.

### AppEngine
The installation of the AppEngine-Project is very straightforward, we tested it on Linux (Arch Linux and Fedora 19) and on Mac OS X. First install Google AppEngine-Plugin for Eclipse (Version 4.3, Kepler), therefore choose _Help => Install new software..._ and add following software site:

```
http://dl.google.com/eclipse/plugin/4.3
```

You have to install the following software from this site:

* Google App Engine Java SDK
* Google Web Toolkit SDK
* Google Plugin for Eclipse 4.3


After installing the plugin and restarting eclipse, import project _appengine_ in the root directory of the repository. Subsequently choose _Debug as_ => _Web Application_ to debug the application locally. To deploy the project simply right-click `Project Explorer` and then choose _Google_ => _Deploy to App Engine_.

Before the application can be deployed, it is necessary to set the application id in the project context menu under _Google_ => _AppEngine Settings..._.
