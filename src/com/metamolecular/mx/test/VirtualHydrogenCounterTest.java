/*
 * MX - Essential Cheminformatics
 * 
 * Copyright (c) 2007-2009 Metamolecular, LLC
 * 
 * http://metamolecular.com/mx
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.metamolecular.mx.test;

import com.metamolecular.mx.model.Atom;
import com.metamolecular.mx.model.DefaultMolecule;
import com.metamolecular.mx.model.Molecule;
import com.metamolecular.mx.model.MoleculeKit;
import com.metamolecular.mx.model.VirtualHydrogenCounter;
import junit.framework.TestCase;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class VirtualHydrogenCounterTest extends TestCase
{
  private VirtualHydrogenCounter counter;

  @Override
  protected void setUp() throws Exception
  {
    counter = new VirtualHydrogenCounter();
  }

  public void testItShouldSayMethanolOxygenHasOneVirtualHydrogen()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c = molecule.addAtom("C");
    Atom o = molecule.addAtom("O");

    molecule.connect(c, o, 1);

    assertEquals(1, counter.countVirtualHydrogens(o));
  }

  public void testItShouldSayHydrogenChlorideHasOneVirtualHydrogen()
  {
    Molecule molecule = new DefaultMolecule();
    Atom cl = molecule.addAtom("Cl");

    assertEquals(1, counter.countVirtualHydrogens(cl));
  }

  public void testItShoudSayMethylamineNitrogenHasTwoVirtualHydrogens()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c = molecule.addAtom("C");
    Atom n = molecule.addAtom("N");

    molecule.connect(c, n, 1);

    assertEquals(2, counter.countVirtualHydrogens(n));
  }

  public void testItShouldSayEthaneCarbonHasThreeVirtualHydrogens()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c1 = molecule.addAtom("C");
    Atom c2 = molecule.addAtom("C");

    molecule.connect(c1, c2, 1);

    assertEquals(3, counter.countVirtualHydrogens(c1));
  }

  public void testItShouldSayHydroxylSingletRadicalHasOneVirtualHydrogen()
  {
    Molecule molecule = new DefaultMolecule();
    Atom o = molecule.addAtom("O");

    o.setRadical(1);

    assertEquals(1, counter.countVirtualHydrogens(o));
  }

  public void testItShouldSayMethylamineSingletRadicalNitrogenHasOneVirtualHydrogen()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c = molecule.addAtom("C");
    Atom n = molecule.addAtom("N");

    n.setRadical(1);
    molecule.connect(c, n, 1);

    assertEquals(1, counter.countVirtualHydrogens(n));
  }

  public void testItShouldSayEthylRadicalCarbonHasTwoHydrogens()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c1 = molecule.addAtom("C");
    Atom c2 = molecule.addAtom("C");

    c2.setRadical(1);
    molecule.connect(c1, c2, 1);

    assertEquals(2, counter.countVirtualHydrogens(c2));
  }

  public void testItShouldSayMethylDoubletRadicalCarbonHasTwoHydrogens()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c = molecule.addAtom("C");

    c.setRadical(2);

    assertEquals(2, counter.countVirtualHydrogens(c));
  }

  public void testItShouldSayChlorineRadicalHasNoHydrogens()
  {
    Molecule molecule = new DefaultMolecule();
    Atom cl = molecule.addAtom("Cl");

    cl.setRadical(1);

    assertEquals(0, counter.countVirtualHydrogens(cl));
  }

  public void testItShouldSayMethylamineAnionNitrogenHasOneHydrogen()
  {
    Molecule molecule = new DefaultMolecule();
    Atom c = molecule.addAtom("C");
    Atom n = molecule.addAtom("N");

    n.setCharge(-1);
    molecule.connect(c, n, 1);

    assertEquals(1, counter.countVirtualHydrogens(n));
  }
  
  public void testItShouldSayTempoOxygenAtomHasNoHydrogens()
  {
    String TEMPO = "[NO NAME]\n  CHEMWRIT          2D\nCreated with ChemWriter - http://metamolecular.com/chemwriter\n 11 11  0  0  0  0  0  0  0  0  0 V2000\n    7.3600   -6.6800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2260   -7.1800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.0921   -6.6800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.0921   -5.6800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2260   -5.1800    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n    7.3600   -5.800     0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.5921   -4.8140    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.0921   -5.6800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    6.8600   -4.8140    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    6.3600   -5.6800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2260   -4.1800    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  1  0  0  0  0\n  3  4  1  0  0  0  0\n  4  5  1  0  0  0  0\n  5  6  1  0  0  0  0\n  6  1  1  0  0  0  0\n  4  7  1  0  0  0  0\n  4  8  1  0  0  0  0\n  6  9  1  0  0  0  0\n  6 10  1  0  0  0  0\n  5 11  1  0  0 \nM  RAD  1  11   1\nM  END";
    Molecule tempo = MoleculeKit.readMolfile(TEMPO);
    
    assertEquals("O", tempo.getAtom(10).getSymbol());
    assertEquals(0, counter.countVirtualHydrogens(tempo.getAtom(10)));
  }
}
