package com.thekdub.lightcontrol.component;

import com.thekdub.lightcontrol.exception.OutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class SubMaster {

  private final int id;
  private final List<Channel> channels = new ArrayList<>();
  private double value = 100.0;

  public SubMaster(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public List<Channel> getChannels() {
    return List.copyOf(channels);
  }

  public void addChannel(Channel channel) {
    channels.add(channel);
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) throws OutOfBoundsException {
    if (value < 0 || value > 100) {
      throw new OutOfBoundsException("SubMaster value must be between 0 and 100.");
    }
    this.value = value;
  }
}
