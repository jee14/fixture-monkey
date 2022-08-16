/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
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

package com.navercorp.fixturemonkey.resolver;

import static com.navercorp.fixturemonkey.Constants.NO_OR_ALL_INDEX_INTEGER_VALUE;
import static com.navercorp.fixturemonkey.api.generator.DefaultNullInjectGenerator.NOT_NULL_INJECT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.property.ElementProperty;
import com.navercorp.fixturemonkey.api.property.Property;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class ContainerElementNodeResolver implements NodeResolver {
	private final NodeResolver nodeResolver;
	private final int sequence;

	public ContainerElementNodeResolver(NodeResolver nodeResolver, int sequence) {
		this.nodeResolver = nodeResolver;
		this.sequence = sequence;
	}

	@Override
	public List<ArbitraryNode> resolve(ArbitraryTree arbitraryTree) {
		List<ArbitraryNode> result = new ArrayList<>();

		List<ArbitraryNode> previousNodes = nodeResolver.resolve(arbitraryTree).stream()
			.flatMap(it -> it.getChildren().stream())
			.collect(Collectors.toList());

		for (ArbitraryNode node : previousNodes) {
			Property property = node.getArbitraryProperty().getProperty();
			if (!(property instanceof ElementProperty)) {
				throw new IllegalArgumentException("Resolved node is not element type. : " + property);
			}

			ElementProperty elementProperty = (ElementProperty)property;
			if (sequence == NO_OR_ALL_INDEX_INTEGER_VALUE || sequence == elementProperty.getSequence()) {
				node.setArbitrary(null);
				node.setArbitraryProperty(node.getArbitraryProperty().withNullInject(NOT_NULL_INJECT));
				result.add(node);
			}
		}

		return result;
	}
}
