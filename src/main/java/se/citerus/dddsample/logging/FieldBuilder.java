package se.citerus.dddsample.logging;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;
import org.jetbrains.annotations.NotNull;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Schedule;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.interfaces.tracking.TrackCommand;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.Instant;
import java.time.format.DateTimeFormatter;


public class FieldBuilder implements com.tersesystems.echopraxia.api.FieldBuilder {
    private static final FieldBuilder INSTANCE = new FieldBuilder();

    public static @NotNull FieldBuilder instance() {
        return INSTANCE;
    }

    public Field apply(TrackingId trackingId) {
        return keyValue("trackingId", trackingIdValue(trackingId));
    }

    @NotNull
    private Value<?> trackingIdValue(TrackingId trackingId) {
        return (trackingId == null) ? Value.nullValue() : Value.string(trackingId.idString());
    }

    public Field destination(Location destination) {
        return keyValue("destination", locationValue(destination));
    }

    public Field apply(Location location) {
        return keyValue("location", locationValue(location));
    }

    private Value<?> locationValue(Location location) {
        if (location == null) {
            return Value.nullValue();
        }
        return Value.object(
            keyValue("locationId", Value.number(location.id)),
            apply(location.unLocode()),
            keyValue("name", Value.string(location.name()))
        );
    }

    public Field apply(UnLocode unLocode) {
       return keyValue("unLocode", unlocadeValue(unLocode));
    }

    private Value<?> unlocadeValue(UnLocode unLocode) {
        return (unLocode != null) ? Value.string(unLocode.idString()) : Value.nullValue();
    }

    public Field apply(Cargo cargo) {
        return keyValue("cargo", cargoValue(cargo));
    }

    private Value<?> cargoValue(Cargo cargo) {
        if (cargo == null) {
            return Value.nullValue();
        }
        return Value.object(
          keyValue("cargoId", Value.number(cargo.id)),
          apply(cargo.delivery()),
          apply(cargo.trackingId()),
          apply(cargo.routeSpecification())
        );
    }

    public Field apply(RouteSpecification routeSpecification) {
        return keyValue("routeSpecification", routeSpecificationValue(routeSpecification));
    }

    private Value<?> routeSpecificationValue(RouteSpecification rs) {
        if (rs == null) {
            return Value.nullValue();
        }
        return Value.object(
                destination(rs.destination()),
                origin(rs.origin()),
                keyValue("arrivalDeadline", instantValue(rs.arrivalDeadline()))
        );
    }

    private Field origin(Location origin) {
        return keyValue("origin", locationValue(origin));
    }

    Field apply(Delivery delivery) {
        return keyValue("delivery", deliveryValue(delivery));
    }

    private Value<?> deliveryValue(Delivery delivery) {
        if (delivery == null) {
            return Value.nullValue();
        }
        return Value.object(
                keyValue("eta", instantValue(delivery.estimatedTimeOfArrival())),
                keyValue("lastKnownLocation", locationValue(delivery.lastKnownLocation())),
                keyValue("isMisdirected", Value.bool(delivery.isMisdirected())),
                keyValue("calculatedAt", instantValue(delivery.calculatedAt()))
        );
    }

    private Value<?> instantValue(Instant instant) {
        return instant == null ? Value.nullValue() : Value.string(DateTimeFormatter.ISO_INSTANT.format(instant));
    }

    public Field apply(HandlingEvent event) {
        return keyValue("handlingEvent", handlingEventValue(event));
    }

    private Value<?> handlingEventValue(HandlingEvent event) {
        if (event == null) {
            return Value.nullValue();
        }
        return Value.object(
            number("handlingEventId", event.id),
            apply(event.type()),
            apply(event.cargo()),
            apply(event.location()),
            completionTime(event.completionTime()),
            registrationTime(event.registrationTime())
        );
    }

    public Field completionTime(Instant completionTime) {
       return keyValue("completionTime", instantValue(completionTime));
    }

    public Field registrationTime(Instant registrationTime) {
        return keyValue("registrationTime", instantValue(registrationTime));
    }

    public Field apply(HandlingEventRegistrationAttempt attempt) {
        return keyValue("handlingEventRegistrationAttempt", handlingEventRegistrationAttemptValue(attempt));
    }

    public Value<?> handlingEventRegistrationAttemptValue(HandlingEventRegistrationAttempt attempt) {
        if (attempt == null) {
            return Value.nullValue();
        }
        return Value.object(
          completionTime(attempt.getCompletionTime()),
          registrationTime(attempt.getRegistrationTime()),
          apply(attempt.getTrackingId()),
          apply(attempt.getUnLocode()),
          apply(attempt.getType()),
          apply(attempt.getVoyageNumber())
        );
    }

    public Field apply(VoyageNumber voyageNumber) {
        return keyValue("voyageNumber", voyageNumberValue(voyageNumber));
    }

    private Value<?> voyageNumberValue(VoyageNumber voyageNumber) {
        return voyageNumber != null ? Value.string(voyageNumber.idString()) : Value.nullValue();
    }

    public Field apply(Voyage voyage) {
        return keyValue("voyage", voyageValue(voyage));
    }

    private Value<?> voyageValue(Voyage voyage) {
        if (voyage == null) {
            return Value.nullValue();
        }
        return Value.object(
          apply(voyage.voyageNumber()),
          apply(voyage.schedule())
        );
    }

    public Field apply(Schedule schedule) {
        return keyValue("schedule", scheduleValue(schedule));
    }

    private Value<?> scheduleValue(Schedule schedule) {
        if (schedule == null) {
            return Value.nullValue();
        }
        return Value.object(
          array("carrierMovements", Value.array(this::carrierMovementValue, schedule.carrierMovements()))
        );
    }

    private Value<?> carrierMovementValue(CarrierMovement cm) {
        if (cm == null) {
            return Value.nullValue();
        }
        return Value.object(
          keyValue("arrivalLocation", locationValue(cm.arrivalLocation())),
          keyValue("arrivalTime", instantValue(cm.arrivalTime())),
          keyValue("departureTime", instantValue(cm.departureTime())),
          keyValue("departureLocation", locationValue(cm.departureLocation()))
        );
    }

    public Field apply(HandlingEvent.Type type) {
        return keyValue("handlingEventType", typeValue(type));
    }

    private Value<?> typeValue(HandlingEvent.Type type) {
        return type != null ? Value.string(type.toString()) : Value.nullValue();
    }

    public Field apply(Message message) {
        return keyValue("jmsMessage", messageValue(message));
    }

    private Value<?> messageValue(Message message) {
        if (message == null) {
            return Value.nullValue();
        }
        try {
            return Value.object(
                    keyValue("jmsMessageId", Value.string(message.getJMSMessageID())),
                    keyValue("jmsTimestamp", Value.number(message.getJMSTimestamp())),
                    keyValue("toString", Value.string(message.toString()))
            );
        } catch (JMSException e) {
            return Value.object(
                keyValue("toString", Value.string(message.toString()))
            );
        }
    }

    public Field apply(Itinerary itinerary) {
        return Field.keyValue("itinerary", itineraryValue(itinerary));
    }

    private Value<?> itineraryValue(Itinerary itinerary) {
        return Value.object(
          array("legs", Value.array(this::legValue, itinerary.legs()))
        );
    }

    private Value<?> legValue(Leg leg) {
        return Value.object(
          keyValue("legId", Value.number(leg.id)),
          apply(leg.voyage.voyageNumber()),
          keyValue("loadLocation", locationValue(leg.loadLocation())),
          keyValue("loadTime", instantValue(leg.loadTime())),
          keyValue("unloadLocation", locationValue(leg.unloadLocation())),
          keyValue("unloadTime", instantValue(leg.unloadTime()))
        );
    }

    public Object apply(TrackCommand trackCommand) {
        return keyValue("trackCommand", trackCommandValue(trackCommand));
    }

    private Value<?> trackCommandValue(TrackCommand trackCommand) {
        return (trackCommand != null)
          ? Value.object(keyValue("trackingId", Value.string(trackCommand.getTrackingId())))
          : Value.nullValue();
    }
}
