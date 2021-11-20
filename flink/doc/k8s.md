Starting a Flink Session on Kubernetes #
Once you have your Kubernetes cluster running and kubectl is configured to point to it, you can launch a Flink cluster in Session Mode via

# (1) Start Kubernetes session
$ ./bin/kubernetes-session.sh -Dkubernetes.cluster-id=my-first-flink-cluster

# (2) Submit example job
$ ./bin/flink run \
--target kubernetes-session \
-Dkubernetes.cluster-id=my-first-flink-cluster \
./examples/streaming/TopSpeedWindowing.jar

# (3) Stop Kubernetes session by deleting cluster deployment
$ kubectl delete deployment/my-first-flink-cluster
Deployment Modes #
For production use, we recommend deploying Flink Applications in the Application Mode, as these modes provide a better isolation for the Applications.

Application Mode #
The Application Mode requires that the user code is bundled together with the Flink image because it runs the user code’s main() method on the cluster. The Application Mode makes sure that all Flink components are properly cleaned up after the termination of the application.

The Flink community provides a base Docker image which can be used to bundle the user code:

FROM flink
RUN mkdir -p $FLINK_HOME/usrlib
COPY /path/of/my-flink-job.jar $FLINK_HOME/usrlib/my-flink-job.jar
After creating and publishing the Docker image under custom-image-name, you can start an Application cluster with the following command:

$ ./bin/flink run-application \
--target kubernetes-application \
-Dkubernetes.cluster-id=my-first-application-cluster \
-Dkubernetes.container.image=custom-image-name \
local:///opt/flink/usrlib/my-flink-job.jar
Note local is the only supported scheme in Application Mode.

The kubernetes.cluster-id option specifies the cluster name and must be unique. If you do not specify this option, then Flink will generate a random name.

The kubernetes.container.image option specifies the image to start the pods with.

Once the application cluster is deployed you can interact with it:

# List running job on the cluster
$ ./bin/flink list --target kubernetes-application -Dkubernetes.cluster-id=my-first-application-cluster
# Cancel running job
$ ./bin/flink cancel --target kubernetes-application -Dkubernetes.cluster-id=my-first-application-cluster <jobId>
You can override configurations set in conf/flink-conf.yaml by passing key-value pairs -Dkey=value to bin/flink.

Per-Job Cluster Mode #
Flink on Kubernetes does not support Per-Job Cluster Mode.

Session Mode #
You have seen the deployment of a Session cluster in the Getting Started guide at the top of this page.

The Session Mode can be executed in two modes:

detached mode (default): The kubernetes-session.sh deploys the Flink cluster on Kubernetes and then terminates.

attached mode (-Dexecution.attached=true): The kubernetes-session.sh stays alive and allows entering commands to control the running Flink cluster. For example, stop stops the running Session cluster. Type help to list all supported commands.

In order to re-attach to a running Session cluster with the cluster id my-first-flink-cluster use the following command:

$ ./bin/kubernetes-session.sh \
-Dkubernetes.cluster-id=my-first-flink-cluster \
-Dexecution.attached=true
You can override configurations set in conf/flink-conf.yaml by passing key-value pairs -Dkey=value to bin/kubernetes-session.sh.

Stop a Running Session Cluster #
In order to stop a running Session Cluster with cluster id my-first-flink-cluster you can either delete the Flink deployment or use:

$ echo 'stop' | ./bin/kubernetes-session.sh \
-Dkubernetes.cluster-id=my-first-flink-cluster \
-Dexecution.attached=true