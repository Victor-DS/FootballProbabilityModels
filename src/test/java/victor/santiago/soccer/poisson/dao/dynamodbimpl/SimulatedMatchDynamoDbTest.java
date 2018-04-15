/*
 * Copyright (c) 2018 victords
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package victor.santiago.soccer.poisson.dao.dynamodbimpl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.collect.Lists;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import victor.santiago.soccer.poisson.model.SimulatedMatch;

@RunWith(MockitoJUnitRunner.class)
public class SimulatedMatchDynamoDbTest {

    @Mock
    private DynamoDBMapper mapper;

    @InjectMocks
    private SimulatedMatchDynamoDb toTest;

    private SimulatedMatch simulatedMatch;

    @Before
    public void setup() {
        simulatedMatch = new SimulatedMatch("League", Instant.now(), "HomeTeam",
                "AwayTeam", 0, 0, new Date());
    }

    @Test
    public void shouldAddSuccessfully() {
        doNothing().when(mapper).save(eq(simulatedMatch));

        toTest.add(simulatedMatch);

        verify(mapper).save(eq(simulatedMatch));
    }

    @Test
    public void shouldAddBatchSuccessfully() {
        List<SimulatedMatch> input = Lists.newArrayList(simulatedMatch);

        doNothing().when(mapper).save(eq(input));

        toTest.add(input);

        verify(mapper).batchSave(eq(input));
    }
}
