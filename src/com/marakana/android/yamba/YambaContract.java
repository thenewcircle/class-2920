package com.marakana.android.yamba;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class YambaContract {
    private YambaContract() {}

    /** The Yamba Content Provider */
    public static final String AUTHORITY = "com.marakana.yamba.content";

    /** Our base URI */
    public static final Uri BASE_URI = new Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .build();

    /** */
    public static final class Timeline {
        /** Our table */
        public static final String TABLE = "timeline";

        /** Our base URI */
        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();

        /** MIME sub-type for timeline content */
        public static final String MIME_SUBTYPE = "/vnd.com.marakana.yamba.timeline";
        /** Timeline table DIR type */
        public static final String DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_SUBTYPE;
        /** Timeline table ITEM type */
        public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MIME_SUBTYPE;

        /**
         * Column definitions for status information.
         */
        public final static class Columns {
            // Prevent instantiation
            private Columns() {}

            /** Required by ListView */
            public static final String ID = BaseColumns._ID;
            /** Actual post time */
            public static final String TIMESTAMP = "timestamp";
            /** User making the post */
            public static final String USER = "user";
            /** Message */
            public static final String STATUS = "status";

            /** Max Timestamp */
            public static final String MAX_TIMESTAMP = "max_timestamp";
        }
    }

    /** The posts table */
    public static final class Posts {
        /** Our table */
        public static final String TABLE = "posts";

        /** Our base URI */
        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();

        /** MIME sub-type for post content */
        public static final String MIME_SUBTYPE = "/vnd.com.marakana.yamba.posts";
        /** Posts table DIR type */
        public static final String DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_SUBTYPE;
        /** Posts table ITEM type */
        public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MIME_SUBTYPE;

        /** A useful table constraint */
        public static final String XACT_CONSTRAINT = Columns.TRANSACTION + "=?";

        /**
         * Column definitions for status information.
         */
        public final static class Columns {
            // Prevent instantiation
            private Columns() {}

            /** Required by ListView */
            public static final String ID = BaseColumns._ID;
            /** Transaction id */
            public static final String TRANSACTION = "xact";
            /** Actual post time or null */
            public static final String TIMESTAMP = "timestamp";
            /** Message */
            public static final String STATUS = "status";
        }
    }
}
