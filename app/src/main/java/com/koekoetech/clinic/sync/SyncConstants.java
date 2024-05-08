package com.koekoetech.clinic.sync;

/**
 * Created by ZMN on 8/17/17.
 **/

public class SyncConstants {

    public static final String AUTHORITY = "com.koekoetech.clinic.sync.UHCContentProvider";

    public static final String SYNC_STATUS_BROADCAST = "status.sync.adapter.processing";

    public static final String SYNC_STATUS = "status";

    public static final String SYNC_STATUS_START = "started";

    public static final String SYNC_STATUS_STOP = "stopped";

    public static final String SYNC_STATUS_PROGRESS = "progress";

    public static final String SYNC_RESULT = "sync_result";

    public static final String SYNC_PROGRESS_MSG = "sync_progress_msg";

    public static final String SYNC_PARAM = "SYNC_TRIGGER";

    public static final int POST_PATIENTS = 1;

    public static final int POST_SUGGESTIONS = 4;

}
