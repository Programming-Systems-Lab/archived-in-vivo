
package edu.columbia.psl.commons.compiler;

/**
 * An {@link Exception} that is associated with an optional {@link Location} in a source file.
 */
public class LocatedException extends CausedException {
    private final Location optionalLocation;

    public LocatedException(String message, Location optionalLocation) {
        super(message);
        this.optionalLocation = optionalLocation;
    }

    public LocatedException(String message, Location optionalLocation, Throwable optionalCause) {
        super(message, optionalCause);
        this.optionalLocation = optionalLocation;
    }

    /**
     * Returns the message specified at creation time, preceeded with nicely formatted location
     * information (if any).
     */
    public String getMessage() {
        return (
            this.optionalLocation == null
            ? super.getMessage()
            : this.optionalLocation.toString() + ": " + super.getMessage()
        );
    }

    /**
     * Returns the {@link Location} object specified at
     * construction time (may be <code>null</code>).
     */
    public Location getLocation() {
        return this.optionalLocation;
    }
}
