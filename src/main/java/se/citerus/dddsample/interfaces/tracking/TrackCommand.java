package se.citerus.dddsample.interfaces.tracking;


import se.citerus.dddsample.logging.FieldBuilder;

public final class TrackCommand {

  /**
   * The tracking id.
   */
  private String trackingId;

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(final String trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public String toString() {
    return FieldBuilder.instance().apply(this).toString();
  }
}
