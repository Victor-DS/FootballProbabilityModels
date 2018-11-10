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

package me.victorsantiago.footballprobabilitymodel.util;

import me.victorsantiago.footballprobabilitymodel.model.League;
import me.victorsantiago.footballprobabilitymodel.model.Match;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public class SerializerUtilTest {

    @Test
    public void shouldParseList() throws IOException {
        List<League> result = SerializerUtil.getLeagues("src/test/resources/sample.json");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Palmeiras", result.get(0).getChampion());
    }

    @Test(expected = NoSuchFileException.class)
    public void shouldNotFindFile() throws Exception {
        SerializerUtil.getLeagues("invalid_path.json");
    }

    @Test
    public void shouldParseListAndAddK() throws Exception {
        final double k = 12.1;
        List<Match> result = SerializerUtil.getMatchesFromLeaguesFilesWithK(k, "src/test/resources/sample.json");
        Assert.assertTrue(result.stream().allMatch(x -> x.getK() == k));
    }

    @Test
    public void shouldNormalizeText() {
        Assert.assertEquals("Sao Paulo", SerializerUtil.normalizeText("São Paulo"));
    }

}
