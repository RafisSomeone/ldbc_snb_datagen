/* 
 Copyright (c) 2013 LDBC
 Linked Data Benchmark Council (http://www.ldbcouncil.org)
 
 This file is part of ldbc_snb_datagen.
 
 ldbc_snb_datagen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 ldbc_snb_datagen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with ldbc_snb_datagen.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 All Rights Reserved.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation;  only Version 2 of the License dated
 June 1991.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package ldbc.snb.datagen.generator.generators.knowsgenerators;

import ldbc.snb.datagen.entities.dynamic.person.Person;
import ldbc.snb.datagen.entities.dynamic.relations.Knows;
import ldbc.snb.datagen.util.GeneratorConfiguration;
import ldbc.snb.datagen.util.RandomGeneratorFarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class RandomKnowsGenerator implements KnowsGenerator {

    private RandomGeneratorFarm randomFarm;
    private Random rand;

    public RandomKnowsGenerator() {
        rand = new Random();
        randomFarm = new RandomGeneratorFarm();
    }

    public void generateKnows(List<Person> persons, int blockId, List<Float> percentages, int step_index, Person.PersonSimilarity personSimilarity) {

        rand.setSeed(blockId);
        List<Integer> stubs = new ArrayList<>();
        int index = 0;
        for (Person p : persons) {
            long degree = Knows.targetEdges(p, percentages, step_index);
            for (int i = 0; i < degree; ++i) {
                stubs.add(index);
            }
            ++index;
        }
        Collections.shuffle(stubs, rand);
        while (!stubs.isEmpty()) {
            int first = rand.nextInt(stubs.size());
            int first_index = stubs.get(first);
            stubs.remove(first);
            if (!stubs.isEmpty()) {
                int second = rand.nextInt(stubs.size());
                int second_index = stubs.get(second);
                stubs.remove(second);
                if (first_index != second_index) {
                    Person p1 = persons.get(first_index);
                    Person p2 = persons.get(second_index);
                    Knows.createKnow(  randomFarm.get(RandomGeneratorFarm.Aspect.DATE),
                            randomFarm.get(RandomGeneratorFarm.Aspect.DELETION_KNOWS),
                            p1,
                            p2, personSimilarity, true);
                }
            }
        }
    }

    @Override
    public void initialize(GeneratorConfiguration conf) {
        // Method inherited from Knows Generator. This specialization is empty because it does not require initizalization
    }
}
