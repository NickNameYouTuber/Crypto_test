package com.example.transauth;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ObjectSerializer {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Object deserialize(String str) {
        try {
            byte[] data = Base64.getDecoder().decode(str);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = objectInputStream.readObject();
            objectInputStream.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
