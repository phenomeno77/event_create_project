package at.qe.skeleton.ui.beans;

import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Basic request scoped bean to test bean initialization.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Named("secTest")
@Scope("request")
public class SecurityTestBean {

    private boolean showOkDialog = false;
    private String performedAction = "NONE";

    public boolean isShowOkDialog() {
        return showOkDialog;
    }

    public String getPerformedAction() {
        return performedAction;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void doAdminAction() {
        performedAction = "ADMIN";
        showOkDialog = true;
    }

    @PreAuthorize("hasAuthority('LOCATION_MANAGER')")
    public void doManagerAction() {
        performedAction = "LOCATION_MANAGER";
        showOkDialog = true;
    }

    @PreAuthorize("hasAuthority('USER')")
    public void doEmployeeAction() {
        performedAction = "USER";
        showOkDialog = true;
    }

    public void doHideOkDialog() {
        showOkDialog = false;
    }

}
