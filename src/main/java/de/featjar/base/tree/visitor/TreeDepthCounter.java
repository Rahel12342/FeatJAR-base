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
package de.featjar.base.tree.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.Traversable;
import java.util.List;
import java.util.Optional;

/**
 * Counts the maximum depth of a tree.
 * Can be passed a class up to which should be counted (e.g., to exclude details in a tree).
 *
 * @author Sebastian Krieter
 */
public class TreeDepthCounter implements TreeVisitor<Traversable<?>, Integer> {
    private Class<? extends Traversable<?>> terminalClass = null;
    private int maxDepth = 0;

    public Class<? extends Traversable<?>> getTerminalClass() {
        return terminalClass;
    }

    public void setTerminalClass(Class<? extends Traversable<?>> terminalClass) {
        this.terminalClass = terminalClass;
    }

    @Override
    public TraversalAction firstVisit(List<Traversable<?>> path) {
        final int depth = path.size();
        if (maxDepth < depth) {
            maxDepth = depth;
        }
        final Traversable<?> node = getCurrentNode(path);
        if ((terminalClass != null) && terminalClass.isInstance(node)) {
            return TraversalAction.SKIP_CHILDREN;
        } else {
            return TraversalAction.CONTINUE;
        }
    }

    @Override
    public void reset() {
        maxDepth = 0;
    }

    @Override
    public Result<Integer> getResult() {
        return Result.of(maxDepth);
    }
}
