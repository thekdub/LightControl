package com.thekdub.lightcontrol.component;

import com.thekdub.lightcontrol.exception.OutOfBoundsException;

import java.util.Objects;

public class Address {

  private final int universe;
  private final int address;
  private int value;

  public Address(int universe, int address) {
    this.universe = universe;
    this.address = address;
  }

  public int getAddress() {
    return address;
  }

  public int getUniverse() {
    return universe;
  }

  public int getSequentialAddress() {
    return address + universe * 512;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) throws OutOfBoundsException {
    if (value < 0 || value > 255) {
      throw new OutOfBoundsException("Address value must be between 0 and 255");
    }
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("%d/%d @ %d", universe, address, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address1 = (Address) o;
    return universe == address1.universe && address == address1.address;
  }

  @Override
  public int hashCode() {
    return Objects.hash(universe, address);
  }
}
