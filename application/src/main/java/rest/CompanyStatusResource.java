package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.CompanyStatusDTO;
import entities.Company;
import entities.CompanyStatusType;
import errorhandling.exceptions.CompanyNotFoundException;
import errorhandling.exceptions.CompanyStatusNotFoundException;
import errorhandling.exceptions.CompanyStatusTypeNotFoundException;
import errorhandling.exceptions.SanitizationException;
import facades.CompanyFacade;
import facades.CompanyStatusFacade;
import facades.CompanyStatusTypeFacade;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("company/status")
public class CompanyStatusResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final CompanyFacade COMPANY_FACADE = CompanyFacade.getCompanyFacade(EMF);
    private static final CompanyStatusFacade COMPANY_STATUS_FACADE = CompanyStatusFacade.getCompanyStatusFacade(EMF);
    private static final CompanyStatusTypeFacade COMPANY_STATUS_TYPE_FACADE = CompanyStatusTypeFacade.getFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<CompanyStatusDTO> companyStatusDTOs = COMPANY_STATUS_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyStatusDTOs))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("id") String id) throws SanitizationException, CompanyStatusNotFoundException {
        UUID uid;

        try {
            uid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        CompanyStatusDTO companyStatusDTO = COMPANY_STATUS_FACADE.getDTOById(uid);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyStatusDTO))
                .build();
    }

    @GET
    @Path("find/type/{type}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByType(@PathParam("type") String type) throws CompanyStatusTypeNotFoundException, SanitizationException {
        CompanyStatusType companyStatusType = COMPANY_STATUS_TYPE_FACADE.getByType(type);

        List<CompanyStatusDTO> companyStatusDTOs = COMPANY_STATUS_FACADE.getAllDTOsByType(companyStatusType);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyStatusDTOs))
                .build();
    }

    @GET
    @Path("find/company/{company}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByCompany(@PathParam("company") String companyId) throws SanitizationException, CompanyNotFoundException {
        UUID uid;

        try {
            uid = UUID.fromString(companyId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        Company company = COMPANY_FACADE.getById(uid);
        List<CompanyStatusDTO> companyStatusDTOs = COMPANY_STATUS_FACADE.getAllDTOsRelatedToCompany(company);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyStatusDTOs))
                .build();
    }

    @GET
    @Path("find/company/{company}/type/{type}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByTypeAndCompany(@PathParam("company") String companyId, @PathParam("type") String type) throws SanitizationException, CompanyNotFoundException, CompanyStatusTypeNotFoundException {
        UUID uid;

        try {
            uid = UUID.fromString(companyId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        Company company = COMPANY_FACADE.getById(uid);
        CompanyStatusType companyStatusType = COMPANY_STATUS_TYPE_FACADE.getByType(type);

        List<CompanyStatusDTO> companyStatusDTOs = COMPANY_STATUS_FACADE.getAllDTOsRelatedToCompanyByType(company, companyStatusType);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyStatusDTOs))
                .build();
    }

    @POST
    @Path("")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response create(String jsonRequestString) {
        // HUSK AT LAVE TESTS!
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response edit(@PathParam("id") String id, String jsonRequestString) {
        // HUSK AT LAVE TESTS!
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

}
