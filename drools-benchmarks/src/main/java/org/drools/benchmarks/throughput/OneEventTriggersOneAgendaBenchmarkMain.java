/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.throughput;

import java.util.concurrent.ExecutionException;

public class OneEventTriggersOneAgendaBenchmarkMain {

    private static final int BENCHMARK_DURATION_IN_SECONDS = 10;

    private static final boolean DO_WARMUP = false;
    private static final int WARMUP_INSERTS = 2_000_000;

    public static void main( String[] args ) {
        OneEventTriggersOneAgendaBenchmark benchmark = new OneEventTriggersOneAgendaBenchmark();
        benchmark.setupKieBase();
        benchmark.setup();
        benchmark.setupCounter();

        if (DO_WARMUP) {
            for ( int i = 0; i < WARMUP_INSERTS; i++ ) {
                benchmark.insertEvent( null , null);
            }

            terminate( benchmark );
            System.gc();
            benchmark.setup();
            benchmark.setupCounter();
        }

        long start = System.nanoTime();
        long end = start + ( BENCHMARK_DURATION_IN_SECONDS * 1_000_000_000L );

        int i = 0;
        while (true) {
            benchmark.insertEvent(null, null);
            i++;
            if (i % 1000 == 0) {
                if (System.nanoTime() > end) {
                    break;
                }
            }
        }

        System.out.println("inserts = " + i);
        System.out.println("firings = " + benchmark.getFiringsCount());

        terminate( benchmark );
    }

    private static void terminate( OneEventTriggersOneAgendaBenchmark benchmark ) {
        try {
            benchmark.tearDown();
            benchmark.stopFireUntilHaltThread();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException( e );
        }
    }
}
