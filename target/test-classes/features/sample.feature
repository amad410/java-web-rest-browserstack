Feature: Sample feature
Background
  Sample background

  @API, @Smoke
  Scenario: SampleAPITest
    Given I call sample api
    Then the result should be OK


  @Mobile, @Smoke
  Scenario: SampleMobileApp Test
    Given I launch app
    Then the app will be visible
