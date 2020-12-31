package pro.bolshakov.vtb.research.schedule.simplespring.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProductClaimDeliveryTask {

    private final long id;
    private final DeliveryStatus status;
    private final int attempt;
    private final LocalDateTime lastChange;
    private final int hashcode;

    public ProductClaimDeliveryTask(long id) {
        this(id, DeliveryStatus.NEW, 0);
    }

    public ProductClaimDeliveryTask(long id, DeliveryStatus status, int attempt) {
        this.id = id;
        this.status = status;
        this.attempt = attempt;
        this.lastChange = LocalDateTime.now();
        this.hashcode = Objects.hash(this.id, this.status, this.attempt, lastChange);
    }

    public long getId() {
        return id;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public ProductClaimDeliveryTask setStatus(DeliveryStatus status) {
        return new ProductClaimDeliveryTask(this.id, status, this.attempt);
    }

    public int getAttempt() {
        return attempt;
    }

    public ProductClaimDeliveryTask setAttempt(int attempt) {
        return new ProductClaimDeliveryTask(this.id, this.status, attempt);
    }

    public LocalDateTime getLastChange() {
        return lastChange;
    }

    public boolean isDelivered(){
        return DeliveryStatus.DELIVERED.equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductClaimDeliveryTask that = (ProductClaimDeliveryTask) o;
        return id == that.id &&
                attempt == that.attempt &&
                status == that.status &&
                Objects.equals(lastChange, that.lastChange);
    }

    @Override
    public int hashCode() {
        return hashcode;
    }
}
