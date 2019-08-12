package com.ysbing.yshare_base.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class ParcelableUtil {
    @NonNull
    public static byte[] marshall(@NonNull Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    @NonNull
    public static Parcel unmarshall(@NonNull byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    @NonNull
    public static <T> T unmarshall(@NonNull byte[] bytes, @NonNull Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}