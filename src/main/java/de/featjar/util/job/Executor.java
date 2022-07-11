/* -----------------------------------------------------------------------------
 * util - Common utilities and data structures
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/util> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.util.job;

import de.featjar.util.data.Result;
import de.featjar.util.job.InternalMonitor.MethodCancelException;

/**
 * Class that can execute instances of {@link MonitorableSupplier} and
 * {@link MonitorableFunction}.<br>
 * Provides a default {@link Monitor} implementation, catches any exceptions
 * thrown by the given function, and wraps the given function's return value in
 * a {@link Result}.
 *
 * @author Sebastian Krieter
 */
public final class Executor {

	private Executor() {
	}

	public static <T> Result<T> run(MonitorableSupplier<T> supplier) {
		return run(supplier, new DefaultMonitor());
	}

	public static <T, R> Result<R> run(MonitorableFunction<T, R> function, T input) {
		return run(function, input, new DefaultMonitor());
	}

	public static <T> Result<T> run(MonitorableSupplier<T> supplier, InternalMonitor monitor) {
		monitor = monitor != null ? monitor : new DefaultMonitor();
		try {
			return Result.of(supplier.execute(monitor));
		} catch (final Exception e) {
			return Result.empty(e);
		} finally {
			monitor.done();
		}
	}

	public static <T, R> Result<R> run(MonitorableFunction<T, R> function, T input, InternalMonitor monitor)
		throws MethodCancelException {
		monitor = monitor != null ? monitor : new DefaultMonitor();
		try {
			return Result.of(function.execute(input, monitor));
		} catch (final Exception e) {
			return Result.empty(e);
		} finally {
			monitor.done();
		}
	}

}