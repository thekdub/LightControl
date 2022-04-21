package com.thekdub.lightcontrol.component;

import com.thekdub.lightcontrol.exception.OutOfBoundsException;

import java.util.LinkedList;
import java.util.List;

public class Channel {

  private final int channel;
  private final LinkedList<Address> addresses = new LinkedList<>();
  private double value = 0.0;

  public Channel(int channel) {
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }

  public List<Address> getAddresses() {
    return List.copyOf(addresses);
  }

  public void addAddress(Address... addresses) {
    this.addresses.addAll(List.of(addresses));
  }

  public void removeAddress(Address... addresses) {
    this.addresses.removeAll(List.of(addresses));
  }

  public void setValue(double value) throws OutOfBoundsException {
    if (value < 0 || value > 100) {
      throw new OutOfBoundsException("Channel value must be between 0 and 100.");
    }
    this.value = value;
    for (Address address : addresses) {
      address.setValue((int) (value / 100.0 * 255));
    }
  }

  public double getValue() {
    return value;
  }
}
