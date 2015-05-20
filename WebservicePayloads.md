# Web Service Payloads #

**Visit XML Payload (Location already exist)**
```
<visit>
  <visitLocation>
    <extId>MAIPOR001</extId>
  </visitLocation>
  <visitDate>26-05-2012</visitDate>
   <roundNumber>1</roundNumber>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
</visit>
```

**Visit XML Payload (Register a new location)**
```
<visit>
  <visitDate>2012-05-29 00:00:00</visitDate>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
  <visitLocation>
    <collectedBy>
      <extId>FWJD1</extId>
    </collectedBy>
    <extId>MAIPOR001</extId>
    <locationName>USM</locationName>
    <locationLevel>
      <extId>POR</extId>
    </locationLevel>
    <locationType>RUR</locationType>
    <locationHead>
      <extid>MAIPOR0010101</extid>
    </locationHead>
  </visitLocation>
  <roundNumber>1</roundNumber>
</visit>
```

**Death Event XML Payload**
```
<death>
  <individual>
    <extid>MAIPOR0020104</extid>
  </individual>
  <house>
    <extId>MAIPOR002</extId>
  </house>
  <household>
    <extId>MAIPOR00201</extId>
  </household>
  <deathPlace>1</deathPlace>
  <deathCause>1</deathCause>
  <visitDeath>
    <extId>VMAIPOR00112</extId>
  </visitDeath>
  <householdName>Household Name</householdName>
  <deceasedName>Decease Name</deceasedName>
  <placeOfDeathOther>Place of Deather Other</placeOfDeathOther>
  <gender>1</gender>
  <deathDate>28-05-2012</deathDate>
  <recordedDate>29-05-2012</recordedDate>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
</death>
```

**In Migration Event**
```
<inmigration>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
  <dateOfInterview>28-05-2012</dateOfInterview>
  <recordedDate>29-05-2012</recordedDate>
  <houseName>The House Name</houseName>
  <house>
    <extId>MAIPOR002</extId>
  </house>
  <householdName>The Household Name</householdName>
  <household>
    <extId>MAIPOR00201</extId>
  </household>
  <visit>
    <extId>VMAIPOR00112</extId>
  </visit>
  <everRegistered>2</everRegistered>
  <individual>
    <extid>MAIPOR0020105</extid>
    <firstName>Paul</firstName>
    <lastName>Doe</lastName>
    <gender>1</gender>
    <dob>29-05-1970</dob>
    <mother>
      <extid>UNK</extid>
    </mother>
    <father>
      <extid>UNK</extid>
    </father>
  </individual>
  <currentIndividualNumber>1</currentIndividualNumber>
  <BIsToA>05</BIsToA>
  <reason>1</reason>
  <reasonOther>Reason Other</reasonOther>
  <origin>1</origin>
  <originOther>Origin Other</originOther>
  <oldNameMovingFrom>Old Name Moving From</oldNameMovingFrom>
  <houseNameMovingFrom>House Name Moving From</houseNameMovingFrom>
  <householdNameMovingFrom>Household Name Moving From</householdNameMovingFrom>
  <sectionMovingFrom>Section Moving From</sectionMovingFrom>
  <villageMovingFrom>Village Moving From</villageMovingFrom>
  <cellPhoneNumberMovingFrom>Cell Phone Moving From</cellPhoneNumberMovingFrom>
  <oldNameFirstReg>Old Name First Reg</oldNameFirstReg>
  <houseNameFirstReg>House Name First Reg</houseNameFirstReg>
  <householdNameFirstReg>Household Name First Reg</householdNameFirstReg>
  <sectionFirstReg>Section First Reg</sectionFirstReg>
  <villageFirstReg>Village First Reg</villageFirstReg>
  <cellPhoneNumberFirstReg>Cell Phone First Reg</cellPhoneNumberFirstReg>
  <formalEducationYears>5</formalEducationYears>
</inmigration>
```

**Out Migration Event Payload**
```
<outmigration>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
  <dateOfInterview>28-05-2012</dateOfInterview>
  <recordedDate>29-05-2012</recordedDate>
  <individual>
    <extid>MAIPOR0020102</extid>
  </individual>
  <nameOfMigrant>Name of Migrant</nameOfMigrant>
  <gender>1</gender>
  <visit>
    <extId>VMAIPOR00112</extId>
  </visit>
  <house>
    <extId>MAIPOR002</extId>
  </house>
  <origin>1</origin>
  <household>
    <extId>MAIPOR00201</extId>
  </household>
  <householdName>Household Name</householdName>
  <placeMovedTo>1</placeMovedTo>
  <placeMovedToOther>Placed Move To Other</placeMovedToOther>
  <houseName>The House Name</houseName>
  <section>The Section</section>
  <village>The Village</village>
  <phoneNumber>The Phone Number</phoneNumber>
  <reason>1</reason>
  <reasonForMigrationOther>The Reason Other</reasonForMigrationOther>
</outmigration>
```

**Pregnancy Observation XML Payload**
```
<pregnancyobservation>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
  <estimatedDateOfConception>15-03-2012</estimatedDateOfConception>
  <recordedDate>29-05-2012</recordedDate>
  <mother>
    <extid>MAIPOR0020103</extid>
  </mother>
  <pregWomanName>Mother Name</pregWomanName>
  <numMonthsOfPregnancy>4</numMonthsOfPregnancy>
  <attendedAnteNatalClinic>1</attendedAnteNatalClinic>
  <healthFacility>1</healthFacility>
  <healthFacilityOther>Health Facility Other</healthFacilityOther>
  <receivedTTInjection>1</receivedTTInjection>
  <firstPregnancy>1</firstPregnancy>
  <visit>
    <extId>VMAIPOR00112</extId>
  </visit>
  <house>
    <extId>MAIPOR002</extId>
  </house>
  <household>
    <extId>MAIPOR00201</extId>
  </household>
  <householdName>Household Name</householdName>
  <reportedBy>1</reportedBy>
</pregnancyobservation>
```

**Pregnancy Outcome XML Payload**
```
<pregnancyoutcome>
  <mother>
    <extid>MAIPOR0020103</extid>
  </mother>
  <nameOfMother>Name of Mother</nameOfMother>
  <motherLineNumber>1</motherLineNumber>
  <collectedBy>
    <extId>FWJD1</extId>
  </collectedBy>
  <recordedDate>01-06-2012</recordedDate>
  <reportedBy>1</reportedBy>
  <house>
    <extId>MAIPOR002</extId>
  </house>
  <household>
    <extId>MAIPOR00201</extId>
  </household>
  <householdName>Household Name</householdName>
  <dobChild>15-05-2012</dobChild>
  <father>
    <extid>JDDJDD0010101</extid>
  </father>
  <nameOfFather>Father Name</nameOfFather>
  <placeOfBirth>1</placeOfBirth>
  <placeOfBirthOther>Place Other</placeOfBirthOther>
  <umbilicalCord>1</umbilicalCord>
  <umbilicalCordCut>1</umbilicalCordCut>
  <firstLiveBirth>1</firstLiveBirth>
  <numberOfLiveBirths>1</numberOfLiveBirths>
  <totalChildrenBorn>1</totalChildrenBorn>
  <numLiveBirths>1</numLiveBirths>
  <visit>
    <extId>VMAIPOR00112</extId>
  </visit>
  <child1>
    <extid>MAIPOR0020107</extid>
    <firstName>James</firstName>
    <lastName>Doe</lastName>
    <gender>1</gender>
  </child1>
  <child2>
    <extid>MAIPOR0020108</extid>
    <firstName>Judy</firstName>
    <lastName>Doe</lastName>
    <gender>2</gender>
  </child2>
</pregnancyoutcome>
```

**Vaccination XML Payload**
```
<vaccination>
    <child>
        <extid>AKPIDUIDD0990105</extid>
    </child>
    <bcg>2012-07-15</bcg>
    <polio1>2012-07-16</polio1>
    <polio2>2012-07-17</polio2>
    <polio3>2012-07-18</polio3>
    <polio4>2012-07-19</polio4>
    <dpt1>2012-07-20</dpt1>
    <dpt2>2012-07-21</dpt2>
    <dpt3>2012-07-22</dpt3>
    <measels>2012-07-23</measels>
    <dropInMouth>1</dropInMouth>
    <injection>1</injection>
    <bcgScar>1</bcgScar>
</vaccination>
```