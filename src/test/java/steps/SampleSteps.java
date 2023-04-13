package steps;

import APIClients.SampleClient;
import Handlers.FileHandler;
import enums.StatusCode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import javassist.tools.rmi.Sample;
import org.junit.Assert;

import java.io.FileNotFoundException;

public class SampleSteps {
    SampleClient _sampleClient;
    public Response _response;
    public SampleSteps(SampleClient sampleClient)
    {
        this._sampleClient = sampleClient;
    }

    @Given("I call sample api")
    public void i_call_sample_api() throws FileNotFoundException {
        _response = this._sampleClient.DoSomething();

    }

    @Then("the result should be OK")
    public void the_result_should_be_ok() {
        Assert.assertTrue("Status code of request is not 200",_response.statusCode() == StatusCode.CODE_200.getCode());
    }




}
