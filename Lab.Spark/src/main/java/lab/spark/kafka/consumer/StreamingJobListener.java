package lab.spark.kafka.consumer;


import org.apache.spark.streaming.scheduler.BatchInfo;
import org.apache.spark.streaming.scheduler.StreamingListener;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchSubmitted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverError;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStopped;
import org.apache.spark.streaming.scheduler.StreamingListenerStreamingStarted;

public class StreamingJobListener implements StreamingListener {

	@Override
	public void onBatchCompleted(StreamingListenerBatchCompleted arg0) {
		BatchInfo batchInfo = arg0.batchInfo();
		//batchInfo.
	}

	@Override
	public void onBatchStarted(StreamingListenerBatchStarted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBatchSubmitted(StreamingListenerBatchSubmitted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOutputOperationCompleted(StreamingListenerOutputOperationCompleted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOutputOperationStarted(StreamingListenerOutputOperationStarted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiverError(StreamingListenerReceiverError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiverStarted(StreamingListenerReceiverStarted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiverStopped(StreamingListenerReceiverStopped arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStreamingStarted(StreamingListenerStreamingStarted arg0) {
		// TODO Auto-generated method stub
		
	}

}
