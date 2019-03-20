/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ic2ExpReactorPlanner;

import java.awt.Image;
import java.util.ResourceBundle;

/**
 * Represents a component in an IndustrialCraft2 Experimental Nuclear Reactor.
 * @author Brian McCloud
 */
public class ReactorComponent {
    
    private Image image = null;
    
    private int row = -10;
    private int column = -10;
    
    private double initialHeat = 0.0;
    protected double currentHeat = 0.0;
    private double maxHeat = 1.0;
    
    private double currentDamage = 0.0;
    private double maxDamage = 1.0;
    
    /**
     * Threshold for heat/damage for removing this component during an automation run.
     */
    private int automationThreshold = 9000;
    
    /**
     * Time to pause the reactor while replacing the component during an automation run.
     */
    private int reactorPause = 0;
    
    private Reactor parent = null;
    
    protected double effectiveVentCooling = 0.0;
    
    protected double bestCondensatorCooling = 0.0;
    
    protected double bestCellCooling = 0.0;
    
    protected double currentCondensatorCooling = 0.0;
    
    protected double currentCellCooling = 0.0;

    protected double minEUGenerated = Double.MAX_VALUE;
    
    protected double maxEUGenerated = 0.0;
    
    protected double minHeatGenerated = Double.MAX_VALUE;
    
    protected double maxHeatGenerated = 0.0;
    
    protected double maxReachedHeat = 0.0;
    
    protected double currentOutput = 0.0;
    
    protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle");
    
    /**
     * Information about this component from the last simulation.
     */
    public StringBuffer info = new StringBuffer(1000);


    /**
     * Gets the name of the component, and the initial heat (if applicable).
     * @return the name of this component, and potentially initial heat.
     */
    @Override
    public String toString() {
        String result = BUNDLE.getString("ComponentName." + this.getClass().getSimpleName());
        if (getInitialHeat() > 0) {
            result += String.format(BUNDLE.getString("UI.InitialHeatDisplay"), (int)getInitialHeat());
        }
        return result;
    }
    
    /**
     * Get the image to show in the planner for this component.
     * @return the image.
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * Set the image to show in the planner for this component.
     * @param image the image to set.
     */
    protected final void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return the row
     */
    public final int getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public final void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the column
     */
    public final int getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public final void setColumn(int column) {
        this.column = column;
    }
    
    /**
     * Checks if this component can accept heat. (e.g. from adjacent fuel rods, or from an exchanger)
     * @return true if this component can accept heat, false otherwise.
     */
    public boolean isHeatAcceptor() {
        return false;
    }
    
    public boolean isNeutronReflector() {
        return false;
    }
    
    /**
     * Prepare for a new reactor tick.
     */
    public void preReactorTick() {
        currentCellCooling = 0.0;
        currentCondensatorCooling = 0.0;
        currentOutput = 0.0;
    }
    
    /**
     * Generate heat if appropriate for component type, and spread to reactor or adjacent cells.
     * @return the amount of heat generated by this component.
     */
    public double generateHeat() {
        return 0.0;
    }
    
    /**
     * Generate energy if appropriate for component type.
     */
    public void generateEnergy() {
        // do nothing by default.
    }
    
    /**
     * Dissipate heat if appropriate for component type.
     */
    public void dissipate() {
        // do nothing by default.
    }
    
    /**
     * Transfer heat between component, neighbors, and/or reactor, if appropriate for component type.
     */
    public void transfer() {
        // do nothing by default.
    }

    /**
     * Apply changes to the reactor when adding this component if appropriate, such as for reactor plating.
     */
    public void addToReactor() {
        // do nothing by default.
    }
    
    /**
     * Apply changes to the reactor when removing this component if appropriate, such as for reactor plating.
     */
    public void removeFromReactor() {
        
    }
    
    /**
     * @return the current heat level of the component.
     */
    public final double getCurrentHeat() {
        return currentHeat;
    }

    /**
     * Resets heat to 0 (used when resetting simulation).
     */
    public final void clearCurrentHeat() {
        currentHeat = initialHeat;
        effectiveVentCooling = 0.0;
        bestCondensatorCooling = 0.0;
        bestCellCooling = 0.0;
        minEUGenerated = Double.MAX_VALUE;
        maxEUGenerated = 0.0;
        minHeatGenerated = Double.MAX_VALUE;
        maxHeatGenerated = 0.0;
        maxReachedHeat = initialHeat;
    }
    
    /**
     * Adjusts the component heat up or down
     * @param heat the amount of heat to adjust by (positive to add heat, negative to remove heat).
     * @return the amount of heat adjustment refused. (e.g. due to going below minimum heat, breaking due to excessive heat, or attempting to remove heat from a condensator)
     */
    public double adjustCurrentHeat(final double heat) {
        if (isHeatAcceptor()) {
            double result = 0.0;
            double tempHeat = getCurrentHeat();
            tempHeat += heat;
            if (tempHeat > getMaxHeat()) {
                result = getMaxHeat() - tempHeat + 1;
                tempHeat = getMaxHeat();
            } else if (tempHeat < 0.0) {
                result = tempHeat;
                tempHeat = 0.0;
            }
            currentHeat = tempHeat;
            maxReachedHeat = Math.max(maxReachedHeat, currentHeat);
            return result;
        }
        return heat;
    }
    
    /**
     * @return the maximum heat the component can take.
     */
    public final double getMaxHeat() {
        return maxHeat;
    }

    /**
     * @param maxHeat the maximum heat the component can take.
     */
    public final void setMaxHeat(final double maxHeat) {
        this.maxHeat = maxHeat;
    }

    /**
     * @return the damage the component has taken.
     */
    public final double getCurrentDamage() {
        return currentDamage;
    }

    /**
     * Clears the damage back to 0 (used when resetting simulation, or replacing the component in an automation simulation).
     */
    public final void clearDamage() {
        currentDamage = 0.0;
    }
    
    /**
     * Applies damage to the component, as opposed to heat.  Mainly used for 
     * fuel rods and neutron reflectors that lose durability as the reactor runs,
     * but can't recover it via cooling.
     * @param damage the damage to apply (only used if positive).
     */
    public final void applyDamage(final double damage) {
        if (damage > 0.0) {
            currentDamage += damage;
        }
    }
    
    /**
     * @return the the maximum damage the component can take.
     */
    public final double getMaxDamage() {
        return maxDamage;
    }

    /**
     * @param maxDamage the maximum damage the component can take.
     */
    public final void setMaxDamage(double maxDamage) {
        this.maxDamage = maxDamage;
    }

    /**
     * Gets the parent reactor.
     * @return the reactor this component is in.
     */
    protected Reactor getParent() {
        return parent;
    }

    /**
     * Sets the parent reactor.
     * @param parent the parent reactor to set
     */
    public void setParent(Reactor parent) {
        this.parent = parent;
    }
    
    /**
     * Determines if this component is broken in the current tick of the simulation
     * @return true if the component has broken either from damage (e.g. neutron reflectors, fuel rods) or from heat (e.g. heat vents, coolant cells), false otherwise.
     */
    public boolean isBroken() {
        return currentHeat >= maxHeat || currentDamage > maxDamage;
    }
    
    /**
     * Gets the materials needed for this component.
     * @return the materials needed for this component.
     */
    public MaterialsList getMaterials() {
        return null;
    }

    /**
     * Gets the initial heat previously set for the component.
     * @return the initial heat.
     */
    public double getInitialHeat() {
        return initialHeat;
    }

    /**
     * Set the initial heat of the component, as long as the component can accept heat, 
     * and the initial heat greater than or equal to zero and less than the max heat.
     * If any condition is false, the value is ignored.
     * @param initialHeat the initial heat to set
     */
    public void setInitialHeat(double initialHeat) {
        if (this.isHeatAcceptor() && initialHeat >= 0 && initialHeat < this.getMaxHeat()) {
            this.initialHeat = initialHeat;
        }
    }
    
    public double getEffectiveVentCooling() {
        return effectiveVentCooling;
    }
    
    public double getVentCoolingCapacity() {
        return 0;
    }

    public double getBestCondensatorCooling() {
        return bestCondensatorCooling;
    }
    
    public double getBestCellCooling() {
        return bestCellCooling;
    }
    
    public double getMinEUGenerated() {
        return minEUGenerated;
    }
    
    public double getMaxEUGenerated() {
        return maxEUGenerated;
    }
    
    public double getMinHeatGenerated() {
        return minHeatGenerated;
    }
    
    public double getMaxHeatGenerated() {
        return maxHeatGenerated;
    }
    
    public double getMaxReachedHeat() {
        return maxReachedHeat;
    }
    
    /**
     * The number of fuel rods in this component (0 for non-fuel-rod components).
     * @return The number of fuel rods in this component, or 0 if this component has no fuel rods.
     */
    public int getRodCount() {
        return 0;
    }
    
    public double getExplosionPowerOffset() {
        if (!isBroken()) {
            return 2 * getRodCount(); // all known fuel rods (including those from GT) use this formula, and non-rod components return 0 for getRodCount
        }
        return 0;
    }
    
    public double getExplosionPowerMultiplier() {
        return 1;
    }

    public final int getAutomationThreshold() {
        return automationThreshold;
    }

    public final void setAutomationThreshold(int automationThreshold) {
        this.automationThreshold = automationThreshold;
    }

    public final int getReactorPause() {
        return reactorPause;
    }

    public final void setReactorPause(int reactorPause) {
        this.reactorPause = reactorPause;
    }

    /**
     * Determines whether this component expects to produces some sort of output each reactor tick,
     * e.g. for purposes of tracking in a CSV file.
     * @return true if this component produces output (such as EU or vented heat), false otherwise.
     */
    public boolean producesOutput() {
        return getVentCoolingCapacity() > 0 || getRodCount() > 0;
    }
    
    public double getCurrentOutput() {
        return currentOutput;
    }
}
