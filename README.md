# jCompoundMapper
Fork of the jCompoundMapper project (ported to CDK 1.5.X and AtomContainer).

Reason for this work is out of personal necessity: trying to parse recent PubChem SDF files (MolFile V2000) actually threw an error every 2 compounds (something about invalid atom energy level). Moving to CDK 1.5 and porting old IMolecule interface to IAtomContainer objects solved the problem.

I tried to keep behavior the same (using legacy aromaticity), but since I don't have any sort of Chemistry degree, I may have modified the behavior of some functions without even realizing it. Please, double-check the results if they seem doubtful to you.

Originally released under LGPL v2.0 (license is kept as is).

Original project at http://sourceforge.net/projects/jcompoundmapper/
