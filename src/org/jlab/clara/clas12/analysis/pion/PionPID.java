package org.jlab.clara.clas12.analysis.pion;

import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataBank;
import sys.ClasServiceEngine;
import org.jlab.hipo.schema.SchemaFactory;
import org.jlab.io.hipo.HipoDataEvent;
import org.jlab.clas.physics.LorentzVector;

/**
 * Pion PID engine.
 *
 * @author kenjo
 * @author gurjyan (put on the new interface, work in progress...)
 */
public class PionPID extends ClasServiceEngine {

    SchemaFactory factory;

    /**
     * Constructor.
     */
    public PionPID() {
        super("PionPID", "kenjo", "1.0", "pion PID");
        factory = new SchemaFactory();
        factory.initFromDirectory("CLAS12DIR", "etc/bankdefs/hipo");
    }

    @Override
    public boolean userInit(String json) {
        return true;
    }

    @Override
    public Object processDataEvent(DataEvent event) {
        if (event instanceof HipoDataEvent) {
            HipoDataEvent hipoEv = (HipoDataEvent) event;
            hipoEv.getHipoEvent().getSchemaFactory().addSchema(factory.getSchema("PHYS::pi0"));

            if (event.hasBank("REC::Particle")) {
                DataBank ebank = event.getBank("REC::Particle");
                for (int iph1 = 0; iph1 < ebank.rows() - 1; iph1++) {
                    if (ebank.getByte("charge", iph1) == 0) {
                        float px = ebank.getFloat("px", iph1);
                        float py = ebank.getFloat("py", iph1);
                        float pz = ebank.getFloat("pz", iph1);
                        LorentzVector pi0 = new LorentzVector();

                        for (int iph2 = iph1 + 1; iph2 < ebank.rows(); iph2++) {
                            if (ebank.getByte("charge", iph2) == 0) {
                        		  pi0.setPxPyPzM(px, py, pz, 0);

                                px = ebank.getFloat("px", iph2);
                                py = ebank.getFloat("py", iph2);
                                pz = ebank.getFloat("pz", iph2);
                                LorentzVector ph2 = new LorentzVector();
                                ph2.setPxPyPzM(px, py, pz, 0);

                                pi0.add(ph2);
                                float mm = (float) pi0.mass();
                                if (mm > 0 && mm < 0.2) {
                                    DataBank physBank = event.createBank("PHYS::pi0", 1);
                                    physBank.setFloat("mass", 0, mm);
                                    event.appendBank(physBank);
                                    return event;
                                }

                            }
                        }
                    }
                }
            }
        }
        return event;
    }
}
