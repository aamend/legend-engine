// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.testable.extension;

import org.eclipse.collections.impl.factory.Lists;
import org.finos.legend.pure.generated.Root_meta_pure_test_Testable;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class TestableRunnerExtensionLoader
{
    private static final AtomicReference<List<TestableRunnerExtension>> INSTANCE = new AtomicReference<>();

    public static List<TestableRunnerExtension> extensions()
    {
        return INSTANCE.updateAndGet(existing ->
        {
            if (existing == null)
            {
                List<TestableRunnerExtension> extensions = Lists.mutable.empty();
                for (TestableRunnerExtension extension : ServiceLoader.load(TestableRunnerExtension.class))
                {
                    extensions.add(extension);
                }
                return extensions;
            }
            else
            {
                return existing;
            }
        });
    }

    public static TestRunner forTestable(Root_meta_pure_test_Testable testable)
    {
        return extensions().stream()
                .map(ext -> ext.getTestRunner(testable))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No testable runner for " + testable.getClass().getSimpleName()));
    }
}
