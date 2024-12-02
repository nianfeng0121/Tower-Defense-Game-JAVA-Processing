package WizardTD;

// Class representing the mana resource in the game
public class Mana {
    double currMana;  // Current amount of mana
    double cap;  // The maximum amount of mana the player can have
    double regenRate;  // The rate at which mana regenerates per second
    double poolCost;  // The cost of using the pool spell
    double poolCostIncrease;  // The amount by which the pool spell cost increases after each use
    double capMultiplier;  // The multiplier applied to the mana cap when the pool spell is used
    double manaMultiplier;  // The multiplier applied to the mana regen rate when the pool spell is used
    double initialRegenRate;  // The initial mana regen rate, used for calculations when the pool spell is used
    int counterOfFrames = 0;  // Counter for the number of frames that have passed

    // Constructor for initializing a new Mana object
    public Mana(
        double initial, double cap, double regenRate, double poolCost, 
        double poolCostIncrease, double capMultiplier, double manaMultiplier
    ) {
        this.currMana = Math.min(initial, cap);  // Set initial mana, ensuring it doesn't exceed the cap
        this.cap = cap;  // Set mana cap
        this.regenRate = regenRate;  // Set mana regeneration rate
        this.initialRegenRate = regenRate;  // Store the initial regen rate for later calculations
        this.poolCost = poolCost;  // Set pool spell cost
        this.poolCostIncrease = poolCostIncrease;  // Set pool spell cost increase rate
        this.capMultiplier = capMultiplier;  // Set mana cap multiplier for pool spell usage
        this.manaMultiplier = manaMultiplier;  // Set mana regen rate multiplier for pool spell usage
    }

    // Getter for current mana
    public double getCurrMana() {
        return this.currMana;
    }

    // Getter for mana cap
    public double getCap() {
        return this.cap;
    }

    // Getter for pool spell cost
    public double getPoolCost() {
        return this.poolCost;
    }

    // Method to handle clicking the pool spell
    public void clickPoolSpell() {
        if (updateMana(-1 * poolCost)) {  // If the player has enough mana to use the spell
            this.cap *= this.capMultiplier;  // Increase mana cap
            this.regenRate += this.manaMultiplier * this.initialRegenRate;  // Increase mana regen rate
            this.poolCost += this.poolCostIncrease;  // Increase pool spell cost
        }
    }
    
    // Method to update the current mana
    public boolean updateMana(double add) {
        if (this.currMana + add <= 0) {  // If the player runs out of mana
            return false;
        } else if (this.currMana + add > this.cap) {  // If adding mana would exceed the cap
            this.currMana = this.cap;  // Set current mana to the cap
            return true;
        } else {
            this.currMana += add;  // Add mana
            return true;
        }
    }

    // Method to set current mana to zero
    public void makeManaZero() {
        this.currMana = 0;
    }

    // Update method, called on each frame to regenerate mana
    public void tick(App app) {
        this.counterOfFrames += app.rate;  // Increase frame counter
        if (this.counterOfFrames >= App.FPS) {  // If one second has passed
            this.updateMana(this.regenRate);  // Regenerate mana
            this.counterOfFrames = 0;  // Reset frame counter
        }
    }
}
