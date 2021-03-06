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

import com.metamolecular.mx.walk.PathWriter;
import com.metamolecular.mx.model.Atom;
import com.metamolecular.mx.model.Bond;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

/**
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class PathWriterTest extends TestCase
{
  private PathWriter writer;
  private Collection<String> paths;
  private Atom atom;
  private Set<Atom> aromatics;
  private Bond bond;

  @Override
  protected void setUp() throws Exception
  {
    atom = mock(Atom.class);
    bond = mock(Bond.class);
    paths = mock(Set.class);
    writer = null;
    aromatics = new HashSet();

    when(atom.getSymbol()).thenReturn(".");
    when(atom.getBonds()).thenReturn(new Bond[0]);
  }

  public void testItClearsPathsOnWalkStart()
  {
    doNew();
    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.walkEnd(atom);
    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.walkEnd(atom);

    verify(paths, times(2)).add(".");
    verify(paths, never()).add("..");
  }

  public void testItWritesAtomAfterAtomFound()
  {
    doNew();
    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.walkEnd(atom);

    verify(paths, times(1)).add(".");
  }

  public void testItWritesTwoAtoms()
  {
    doNew();
    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.bondFound(bond);
    writer.atomFound(atom);
    writer.walkEnd(atom);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths).add(".");
    sequence.verify(paths).add("..");
  }

  public void testItThrowsIfTwoAtomsNotSeparatedByBond()
  {
    doNew();
    writer.walkStart(atom);
    writer.atomFound(atom);
    try
    {
      writer.atomFound(atom);
      fail("Exception not thrown");
    }
    catch (Exception e)
    {
    }
  }

  public void testItWritesTwoAtomsSeparatedByADoubleBond()
  {
    doNew();
//    Bond bond = mock(Bond.class);
    when(bond.getType()).thenReturn(2);
    when(atom.getBonds()).thenReturn(new Bond[]
      {
        bond
      });

    Atom atom1 = mockAtom(".");
        when(bond.getMate(atom)).thenReturn(atom1);
    when(atom1.getBonds()).thenReturn(new Bond[]
      {
        bond
      });

    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.bondFound(bond);
    writer.atomFound(atom1);
    writer.walkEnd(atom1);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths).add(".%");
    sequence.verify(paths).add(".%.%");
  }
  public void testItWritesSaturatedAtomForFirstAtomOfDoubleBond()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Bond bond1 = mock(Bond.class);
    Bond bond2 = mock(Bond.class);

    when(bond1.getType()).thenReturn(1);
    when(bond2.getType()).thenReturn(2);
    when(bond2.getMate(atom2)).thenReturn(atom3);
    when(atom1.getBonds()).thenReturn(new Bond[]
      {
        bond1
      });
    when(atom2.getBonds()).thenReturn(new Bond[]
      {
        bond1, bond2
      });
    when(atom3.getBonds()).thenReturn(new Bond[]
      {
        bond2
      });

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond1);
    writer.atomFound(atom2);
    writer.bondFound(bond2);
    writer.atomFound(atom3);
    writer.walkEnd(atom1);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths, times(1)).add("1");
    sequence.verify(paths, times(1)).add("12");
    sequence.verify(paths, times(1)).add("12%");
    sequence.verify(paths, times(1)).add("12%3%");

    verify(paths, times(4)).add(any(String.class));
  }

  public void testItWritesDoubleBondForRingClosure()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Bond bond1 = mock(Bond.class);
    Bond bond2 = mock(Bond.class);
    Bond bond3 = mock(Bond.class);

    when(bond1.getType()).thenReturn(1);
    when(bond2.getType()).thenReturn(1);
    when(bond3.getType()).thenReturn(2);
    when(bond3.getMate(atom3)).thenReturn(atom1);
    when(atom1.getBonds()).thenReturn(new Bond[]
      {
        bond1, bond3
      });
    when(atom2.getBonds()).thenReturn(new Bond[]
      {
        bond1, bond2
      });
    when(atom3.getBonds()).thenReturn(new Bond[]
      {
        bond2, bond3
      });

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond1);
    writer.atomFound(atom2);
    writer.bondFound(bond2);
    writer.atomFound(atom3);
    writer.bondFound(bond3);
    writer.ringClosed(bond3);
    writer.walkEnd(atom1);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths, times(1)).add("1%");
    sequence.verify(paths, times(1)).add("1%2");
    sequence.verify(paths, times(1)).add("1%23%");
    sequence.verify(paths, times(1)).add("1%23%-3");

    verify(paths, times(4)).add(any(String.class));
  }
  public void testItClearsBondPathWhenBranchStarted()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Bond bond1 = mock(Bond.class);
    Bond bond2 = mock(Bond.class);

    when(bond1.getType()).thenReturn(1);
    when(atom1.getBonds()).thenReturn(new Bond[]
      {
        bond1
      });
    when(atom2.getBonds()).thenReturn(new Bond[]
      {
        bond1, bond2
      });
    when(atom3.getBonds()).thenReturn(new Bond[]
      {
        bond2
      });

    writer.walkStart(atom2);
    writer.atomFound(atom2);
    writer.bondFound(bond1);
    writer.atomFound(atom1);
    writer.branchStart(atom2);
    writer.bondFound(bond2);
    writer.atomFound(atom3);
    writer.branchEnd(atom2);
    writer.walkEnd(atom2);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths, times(1)).add("2");
    sequence.verify(paths, times(1)).add("21");
    sequence.verify(paths, times(1)).add("2");
    sequence.verify(paths, times(1)).add("23");

    verify(paths, times(4)).add(any(String.class));
  }

  public void testItBacktracksWhenBranchStarted()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom2);
    writer.branchStart(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom3);
    writer.branchEnd(atom1);
    writer.walkEnd(atom1);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths, times(1)).add("1");
    sequence.verify(paths, times(1)).add("12");
    sequence.verify(paths, times(1)).add("13");
  }

  public void testItThrowsWhenBranchingFromNonexistantAtom()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");

    writer.atomFound(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom2);

    try
    {
      writer.branchStart(atom3);
      fail("Exception not thrown");
    }
    catch (RuntimeException e)
    {
    }
  }

  public void testItWritesAromaticAtoms()
  {
    doNew();
    aromatics.add(atom);
    writer.setAromatics(aromatics);
    writer.walkStart(atom);
    writer.atomFound(atom);
    writer.walkEnd(atom);

    verify(paths, times(1)).add(".%");
  }

  public void testItWritesRingClosure()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Bond closure = mock(Bond.class);

    when(closure.getMate(atom3)).thenReturn(atom1);

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom2);
    writer.bondFound(bond);
    writer.atomFound(atom3);
    writer.ringClosed(closure);
    writer.walkEnd(atom1);

    InOrder sequence = inOrder(paths);

    sequence.verify(paths, times(1)).add("1");
    sequence.verify(paths, times(1)).add("12");
    sequence.verify(paths, times(1)).add("123");
    sequence.verify(paths, times(1)).add("123-3");
  }

  public void testItWritesRingClosureWhenNoAtomsInBranch()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Atom atom4 = mockAtom("4");
    Bond closure = mock(Bond.class);

    when(closure.getMate(atom3)).thenReturn(atom1);

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom2);
    writer.bondFound(bond);
    writer.atomFound(atom3);
    writer.bondFound(bond);
    writer.atomFound(atom4);
    writer.branchStart(atom3);
    writer.ringClosed(closure);
    writer.branchEnd(atom3);
    writer.walkEnd(atom1);

    verify(paths, times(1)).add("123-3");
  }

  public void testItWritesLongestLinearPathOnceWhenRingClosed()
  {
    doNew();

    Atom atom1 = mockAtom("1");
    Atom atom2 = mockAtom("2");
    Atom atom3 = mockAtom("3");
    Bond closure = mock(Bond.class);

    when(closure.getMate(atom3)).thenReturn(atom1);

    writer.walkStart(atom1);
    writer.atomFound(atom1);
    writer.bondFound(bond);
    writer.atomFound(atom2);
    writer.bondFound(bond);
    writer.atomFound(atom3);
    writer.ringClosed(closure);
    writer.walkEnd(atom1);

    verify(paths, times(1)).add("123");
  }

  public void testItRaisesWhenClosingToEmptyPath()
  {
    doNew();

    Bond closure = mock(Bond.class);

    try
    {
      writer.ringClosed(closure);

      fail("Exception not thrown");
    }
    catch (Exception e)
    {
      assertEquals("Attempt to close empty path.", e.getMessage());
    }
  }

  public void testItThrowsWhenClosingToNonexistantAtom()
  {
    doNew();

    Bond closure = mock(Bond.class);
    Atom maverick = mock(Atom.class);

    when(closure.getMate(atom)).thenReturn(maverick);

    writer.atomFound(atom);
    writer.bondFound(bond);
    writer.atomFound(atom);
    writer.bondFound(bond);
    writer.atomFound(atom);

    try
    {
      writer.ringClosed(closure);

      fail("Exception not thrown");
    }
    catch (Exception e)
    {
      assertEquals("Attempt to close nonexistant atom" + maverick, e.getMessage());
    }
  }

  public void testItRaisesWhenClosingToNonRingAtom()
  {
    doNew();

    Bond closure = mock(Bond.class);
    Atom shorty = mock(Atom.class);

    when(closure.getMate(atom)).thenReturn(shorty);

    writer.atomFound(shorty);
    writer.bondFound(bond);
    writer.atomFound(atom);

    try
    {
      writer.ringClosed(closure);

      fail("Exception not thrown");
    }
    catch (Exception e)
    {
      assertEquals("Atom closes rings with size less than three " + shorty, e.getMessage());
    }
  }
  private void doNew()
  {
    writer = new PathWriter(paths);
  }

  private Atom mockAtom(String label)
  {
    Atom result = mock(Atom.class);

    when(result.getSymbol()).thenReturn(label);
    when(result.getBonds()).thenReturn(new Bond[0]);

    return result;
  }
}
