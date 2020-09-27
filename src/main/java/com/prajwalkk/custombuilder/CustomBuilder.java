package com.prajwalkk.custombuilder;/*
 *
 * Created by: prajw
 * Date: 27-Sep-20
 *
 */

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TableColumn;

import java.util.List;

// Directly copied from the extended class CloudletsTableBuilder.java
public class CustomBuilder extends CloudletsTableBuilder {
    private static final String TIME_FORMAT = "%.0f";
    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";
    private static final String CURRENCY_FORMAT = "%.2f";


    public CustomBuilder(List<? extends Cloudlet> list) {
        super(list);
    }

    @Override
    protected void createTableColumns() {
        final String ID = "ID";
        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
        addColumnDataFunction(getTable().addColumn("Status "), task -> task.getStatus().name());
        addColumnDataFunction(getTable().addColumn("DC", ID), task -> task.getVm().getHost().getDatacenter().getId());
        addColumnDataFunction(getTable().addColumn("Host", ID), task -> task.getVm().getHost().getId());
        addColumnDataFunction(getTable().addColumn("Host PEs ", CPU_CORES), task -> task.getVm().getHost().getWorkingPesNumber());
        addColumnDataFunction(getTable().addColumn("VM", ID), task -> task.getVm().getId());
        addColumnDataFunction(getTable().addColumn("VM PEs   ", CPU_CORES), task -> task.getVm().getNumberOfPes());
        addColumnDataFunction(getTable().addColumn("CloudletLen", "MI"), Cloudlet::getLength);
        addColumnDataFunction(getTable().addColumn("CloudletPEs", CPU_CORES), Cloudlet::getNumberOfPes);

        TableColumn col = getTable().addColumn("StartTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, Cloudlet::getExecStartTime);

        col = getTable().addColumn("FinishTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, task -> roundTime(task, task.getFinishTime()));

        col = getTable().addColumn("ExecTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, task -> roundTime(task, task.getActualCpuTime()));

        col = getTable().addColumn("Ram Utilized", "%").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getUtilizationOfRam());

        col = getTable().addColumn("Cost / Bw", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getCostPerBw());

        col = getTable().addColumn("Cost / s", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getCostPerSec());

        col = getTable().addColumn("Accumulated Bw Cost", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getAccumulatedBwCost());

        col = getTable().addColumn("Total Cost", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getTotalCost());

    }

    private double roundTime(final Cloudlet cloudlet, final double time) {


        if (time - cloudlet.getExecStartTime() < 1) {
            return time;
        }

        final double startFraction = cloudlet.getExecStartTime() - (int) cloudlet.getExecStartTime();
        return Math.round(time - startFraction);
    }
}
