# Peptides

# Peptides digestion

## Proteotypic peptides (PTPs)
Proteotypic peptides are defined as the peptides that uniquely identify each protein and are consistently observed when a 
sample mixture is interrogated by a (tandem) mass spectrometer [\[1\]](#references)</sup>.

The list of PTPs can be created in many different ways. The selected methods are the following.

#### Method 1: GPMDB + UniProt

* Download the [human PTPs list](http://www.thegpm.org/lists/index.html) from [GPMDB](http://gpmdb.thegpm.org/).
~~~~
ftp://ftp.thegpm.org/projects/xhunter/libs/eukaryotes/peptide/human_chromosomes/
~~~~
* Query uniprot to get the corresponding protein for each PTP.

#### Method 2: Peptide Atlas 

* Download the current build for all human peptides [here](https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/defaultBuildsPepsProts) or [here](https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/defaultBuildsPepsProts).
* Get all proteins contained in reactome
~~~~
MATCH (re:ReferenceEntity)<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)
WHERE re.databaseName = 'UniProt' AND ewas.speciesName = "Homo sapiens"
RETURN DISTINCT re.identifier as UniProtAccession
ORDER BY UniProtAccession
~~~~
* Map all the peptides to Uniprot accessions using PeptideMapper.
* Filter to only peptides mapped to proteins in Reactome 
* Filter to only PTP

#### Method 3: ProteomeTools / ProteomicsDB (in PRIDE)

The Peptide Sets contained in ProteomeTools are explained [here](http://www.proteometools.org/index.php?id=49).
The set contains 124,875 tryptic peptides covering 15,855 protein coding human genes (by SwissProt) that have been frequently and confidently identified in ProteomicsDB.org. This data is designated as “TUM_first_pool” in PRIDE.

* Download the PTPs list from the [PRIDE Repository](http://www.ebi.ac.uk/pride/archive/projects/PXD004732)

# Predictors

* [PeptideRank](http://wlab.ethz.ch/peptiderank/) [(pubmed)](http://www.ncbi.nlm.nih.gov/pubmed/24878426)
* [dbtoolkit](https://github.com/compomics/dbtoolkit)
* [Trypsin Protein Cleaver](https://dtai.cs.kuleuven.be/software/trypsin)
* [ProteinProspector](http://prospector.ucsf.edu/prospector/mshome.htm)

# References
\[1\] [Mallick P et al (2007) Computational prediction of proteotypic peptides for quantitative proteomics. Nat Biotechnol 25(1):125–31](http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17195840) <br>


