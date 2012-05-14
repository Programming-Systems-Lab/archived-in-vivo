


package edu.columbia.psl.invivoexpreval;

import edu.columbia.psl.commons.compiler.Location;

/**
 * Interface type for {@link UnitCompiler#setWarningHandler(WarningHandler)}.
 */
public interface WarningHandler {
    void handleWarning(String handle, String message, Location optionalLocation);
}
