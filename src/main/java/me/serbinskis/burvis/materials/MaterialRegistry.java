package me.serbinskis.burvis.materials;

import me.serbinskis.burvis.materials.gasses.elements.Air;
import me.serbinskis.burvis.materials.liquids.elements.Water;
import me.serbinskis.burvis.materials.solids.elements.Stone;
import me.serbinskis.burvis.materials.solids.elements.Sand;

import java.util.HashMap;
import java.util.function.Supplier;

public class MaterialRegistry {
    private static final HashMap<String, MaterialSupplier> materialRegistry = new HashMap<>();
    public static final MaterialSupplier AIR = register(new MaterialSupplier(Air::new));
    public static final MaterialSupplier SAND = register(new MaterialSupplier(Sand::new));
    public static final MaterialSupplier STONE = register(new MaterialSupplier(Stone::new));
    public static final MaterialSupplier WATER = register(new MaterialSupplier(Water::new));

    public static MaterialSupplier register(MaterialSupplier materialSupplier) {
        materialRegistry.put(materialSupplier.get().getName(), materialSupplier);
        return materialSupplier;
    }

    public static class MaterialSupplier implements Supplier<Material> {
        private final Supplier<Material> delegate;

        public MaterialSupplier(Supplier<Material> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Material get() {
            return delegate.get();
        }

        public boolean equals(Material obj) {
            return this.get().equals(obj);
        }
    }
}
