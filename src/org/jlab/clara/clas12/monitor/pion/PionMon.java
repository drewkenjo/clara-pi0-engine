package org.jlab.clara.clas12.monitor.pion;

import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataBank;
import sys.ClasServiceEngine;

/**
 * Pion Monitoring engine.
 *
 * @author kenjo
 * @author gurjyan (put on the new interface, work in progress...)
 */
public class PionMon extends ClasServiceEngine {

    /**
     * Constructor.
     */
    public PionMon() {
        super("PionMon", "kenjo", "1.0", "pi0 Mon");
    }

    @Override
    public boolean userInit(String json) {
        return true;
    }

    @Override
    public Object processDataEvent(DataEvent event) {
        if (event.hasBank("PHYS::pi0")) {
            DataBank ebank = event.getBank("PHYS::pi0");
            float mm = ebank.getFloat("mass", 0);
            addTsObservable("PHYS::pi0::mass", mm);
            if (mm > 0.08 && mm < 0.2) {
                addTsObservable("PHYS::pi0::masspeak", mm);
            }
            publishTsObservables();
        }
        return event;
    }

}
