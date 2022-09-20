/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of util.
 *
 * util is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with util. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-util> for further information.
 */
package de.featjar.base.extension;

/**
 * An extension implements some functionality that can be loaded by an {@link ExtensionPoint}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Extension {

    /**
     * {@return a unique identifier for this extension}
     */
    default String getIdentifier() {
        return getClass().getCanonicalName();
    }

    /**
     * Initializes this extension, called by {@link ExtensionPoint}.
     *
     * @return whether the initialization was successful
     */
    default boolean install() {
        return true;
    }

    /**
     * Deinitializes this extension, called by {@link ExtensionManager}.
     */
    default void uninstall() {
    }
}