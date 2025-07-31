package com.rmq.example.subscriber.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class QueueMessage {
    @JsonProperty("key1")
    private String key1;

    @JsonProperty("key2")
    public String key2;

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QueueMessage that = (QueueMessage) o;
        return Objects.equals(key1, that.key1) && Objects.equals(key2, that.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }

    @Override
    public String toString() {
        return "QueueMessage{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                '}';
    }
}
