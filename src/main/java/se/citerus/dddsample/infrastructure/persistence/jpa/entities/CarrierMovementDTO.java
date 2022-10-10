package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "CarrierMovement")
@Table(name = "CarrierMovement")
public class CarrierMovementDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_location_id", nullable = false)
    public LocationDTO arrivalLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_location_id", nullable = false)
    public LocationDTO departureLocation;

    @Column(name = "arrival_time", nullable = false)
    public Date arrivalTime;

    @Column(name = "departure_time", nullable = false)
    public Date departureTime;

    public CarrierMovementDTO() {
    }

    public CarrierMovementDTO(LocationDTO arrivalLocation, LocationDTO departureLocation, Date arrivalTime, Date departureTime) {
        this.arrivalLocation = arrivalLocation;
        this.departureLocation = departureLocation;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }
}
