package my.projects.salacrycalculator.services;

import javassist.bytecode.stackmap.TypeData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VatThread implements Runnable {

    private final Path vatPath;
    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());

    public VatThread(Path vatPath) {
        this.vatPath = vatPath;
    }

    @Override
    public void run() {
        File vatFile = new File(vatPath.toString());

        if (!vatFile.exists() && !vatFile.isDirectory()) {
            try {
                Files.createFile(vatPath);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating a vat file", e);
            }
        }

        if (vatFile.length() == 0) {
            List<BigDecimal> vats = new ArrayList<>();
            vats.add(new BigDecimal(25).setScale(0, RoundingMode.HALF_UP));
            vats.add(new BigDecimal(20).setScale(0, RoundingMode.HALF_UP));
            vats.add(new BigDecimal(19).setScale(0, RoundingMode.HALF_UP));

            FileWriter writer = null;
            try {
                writer = new FileWriter(vatFile);
                for (int i = 0; i < vats.size(); i++) {
                    try {
                        writer.append(vats.get(i).toString());
                        if (i != vats.size() - 1) writer.append(",");
                    } catch (IOException e) {
                        LOG.log(Level.SEVERE, "Problem with writing to a vat file", e);
                    }
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating FileWriter to vat file", e);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Problem with closing FileWriter for vat file", ioe);
                    }
                }
            }
        }
    }
}
