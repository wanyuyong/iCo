package magic.yuyong.request;

public class RequestState {
	public int requestType;
	public boolean isRequest;
	public boolean isBottom;
	public boolean isRefresh;
	public boolean isFirstTime = true;
	public long maxId;
	public int page = 1;
	public String response;
	
	public RequestState() {
		super();
	}

	public RequestState(int requestType) {
		super();
		this.requestType = requestType;
	}

}
