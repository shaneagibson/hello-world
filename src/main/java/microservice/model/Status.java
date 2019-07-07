package microservice.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Status {

    private final String version;
    private final String status;

    private Status(final String version, final String status) {
        this.version = version;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public static Status ok(final String version) {
        return new Status(version, "OK");
    }

    public static Status incident(final String version) {
        return new Status(version, "INCIDENT");
    }


    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}