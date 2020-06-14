package client_side.ui.views;

import client_side.ui.view_models.MainWindowViewModel;
import client_side.ui.view_models.MainWindowViewModelImpl;

public interface MainWindowView {

    //void setPlanePos(double xKm,double yKm);
    void setViewModel(MainWindowViewModelImpl vm);
    //void setPlaneAngle(double heading);

}
