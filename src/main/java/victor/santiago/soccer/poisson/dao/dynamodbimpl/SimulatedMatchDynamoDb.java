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

import com.amazonaws.SdkBaseException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.inject.Inject;

import java.util.List;

import lombok.RequiredArgsConstructor;

import victor.santiago.soccer.poisson.dao.SimulatedMatchDAO;
import victor.santiago.soccer.poisson.model.SimulatedMatch;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SimulatedMatchDynamoDb implements SimulatedMatchDAO {

    private final DynamoDBMapper mapper;

    @Override
    public void add(SimulatedMatch simulatedMatch) throws SdkBaseException {
        mapper.save(simulatedMatch);
    }

    @Override
    public List<DynamoDBMapper.FailedBatch> add(List<SimulatedMatch> simulatedMatches) throws SdkBaseException {
        return mapper.batchSave(simulatedMatches);
    }
}
