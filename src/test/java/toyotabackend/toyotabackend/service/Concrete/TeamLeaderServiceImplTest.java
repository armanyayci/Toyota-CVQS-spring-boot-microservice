package toyotabackend.toyotabackend.service.Concrete;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import toyotabackend.toyotabackend.TestUtils;
import toyotabackend.toyotabackend.dao.VehicleDefectLocationRepository;
import toyotabackend.toyotabackend.dao.VehicleDefectRepository;
import toyotabackend.toyotabackend.domain.Vehicle.TT_Defect_Location;
import toyotabackend.toyotabackend.domain.Vehicle.TT_Vehicle_Defect;
import toyotabackend.toyotabackend.dto.response.DefectViewResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TeamLeaderServiceImplTest extends TestUtils {

    private TeamLeaderServiceImpl teamLeaderService;
    private VehicleDefectRepository vehicleDefectRepository;
    private VehicleDefectLocationRepository vehicleDefectLocationRepository;

    @BeforeAll
    public static void loadOpenCV() {
        System.load("C:/Users/arman/Documents/GitHub/toyota-backend/opencv/build/java/x64/opencv_java470.dll");

    }
    @BeforeEach
    void SetUp(){
        vehicleDefectRepository = mock(VehicleDefectRepository.class);
        vehicleDefectLocationRepository = mock(VehicleDefectLocationRepository.class);
        teamLeaderService = new TeamLeaderServiceImpl(vehicleDefectRepository,vehicleDefectLocationRepository);
    }


    @Test
    public void drawById_whenCalledWithVehicleDefectId_itShouldReturnByteOfImage(){

        int defectId = 1;
        TT_Vehicle_Defect defect = generateVehicleDefect();

        String imagePath = "C:\\Users\\arman\\Desktop\\files\\img.jpeg";
        Mat image = Imgcodecs.imread(imagePath);
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpeg", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        defect.setImg(byteArray);

        when(vehicleDefectRepository.findById(defectId)).thenReturn(Optional.of(defect));
        List<TT_Defect_Location> locations = generateListOfDefectLocation();
        when(vehicleDefectLocationRepository.findLocationByDefectId(defectId)).thenReturn(locations);

        var result = teamLeaderService.drawById(defectId);
        assertNotNull(result);
    }

    @Test
    public void drawById_whenCalledWithNotExistVehicleDefect_itShouldThrowNullPointerException(){

        TT_Vehicle_Defect defect = generateVehicleDefect();
        when(vehicleDefectRepository.findById(defect.getId())).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class,() -> teamLeaderService.drawById(defect.getId()));
    }

    @Test
    public void getListOfVehicleDefectsWithName_whenCalledWithVehicleNameAndOtherOptionalParameters_itShouldReturnListOfDefectViewResponse(){
        String name = "testName";
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 2022;

        List<TT_Vehicle_Defect> defects = generateListOfVehicleDefects();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findDefectsByVehicleNameWithFilter(name, paging, filterYear)).thenReturn(defectPage);
        List<DefectViewResponse> result = teamLeaderService.getListOfVehicleDefectsWithName
                (name,pageNo,pageSize,sortBy,filterYear);

        assertNotNull(result);
        assertEquals(2,result.size());
    }

    @Test
    public void getListOfVehicleDefectsWithName_whenCalledWithVehicleNameWithoutFilterYear_itShouldReturnListOfDefectViewResponse(){
        String name = "testName";
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 0;

        List<TT_Vehicle_Defect> defects = generateListOfVehicleDefects();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findDefectsByVehicleId(name, paging)).thenReturn(defectPage);
        List<DefectViewResponse> result = teamLeaderService.getListOfVehicleDefectsWithName
                (name,pageNo,pageSize,sortBy,filterYear);

        verify(vehicleDefectRepository,times(0)).findDefectsByVehicleNameWithFilter(name, paging, filterYear);
        assertNotNull(result);
        assertEquals(2,result.size());
    }

    @Test
    public void getListOfVehicleDefectsWithName_whenCalledNotExistDefectList_itShouldThrowNullPointerException(){

        String name = "testName";
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 0;

        List<TT_Vehicle_Defect> defects = new ArrayList<>();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findDefectsByVehicleId(name, paging)).thenReturn(defectPage);
        var result = teamLeaderService.getListOfVehicleDefectsWithName(name,pageNo,pageSize,sortBy,filterYear);

        assertEquals(result,new ArrayList<>());
        assertTrue(defectPage.isEmpty());
    }
    @Test
    public void getListOfDefects_whenCalledWithOptionalParametersWithoutFilterYear_itShouldReturnListOfDefectViewResponse(){

        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 0;

        List<TT_Vehicle_Defect> defects = generateListOfVehicleDefects();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findAll(paging)).thenReturn(defectPage);
        List<DefectViewResponse> result = teamLeaderService.getListOfDefects(pageNo,pageSize,sortBy,filterYear);

        assertNotNull(result);
        assertEquals(2,result.size());
    }
    @Test
    public void getListOfDefects_whenCalledWithOptionalParametersWithFilterYear_itShouldReturnFilteredListOfDefectViewResponse(){

        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 2023;

        List<TT_Vehicle_Defect> defects = generateListOfVehicleDefects();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findAllDefectWithFilter(paging,filterYear)).thenReturn(defectPage);
        List<DefectViewResponse> result = teamLeaderService.getListOfDefects(pageNo,pageSize,sortBy,filterYear);

        assertNotNull(result);
        assertEquals(2,result.size());
    }


    @Test
    public void getListOfDefects_whenCalledNotExistDefectList_itShouldThrowNullPointerException(){

        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        int filterYear = 0;

        List<TT_Vehicle_Defect> defects = new ArrayList<>();
        Page<TT_Vehicle_Defect> defectPage = new PageImpl<>(defects);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        when(vehicleDefectRepository.findAll(paging)).thenReturn(defectPage);
        var result = teamLeaderService.getListOfDefects(pageNo,pageSize,sortBy,filterYear);

        assertEquals(result,new ArrayList<>());
        assertTrue(defectPage.isEmpty());
    }

}















