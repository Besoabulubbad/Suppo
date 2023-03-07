/* eslint-disable react-native/no-inline-styles */
import { TouchableOpacity } from '@gorhom/bottom-sheet';
import { Header } from '@react-navigation/stack';
import { position } from '@shopify/restyle';
import { client } from 'api/client';
import { dashboardTask, refreshToken, useTasks } from 'api/useTasks';
import jwt_decode from "jwt-decode";

import {
  getAcessToken,
  getName, getREFRESH, getSelectedLI, getSelectedOP, getToken,
  getUserName, getUserRole, getUserRoleId, getUserRoles, getUserToken, LOGGEDIN, REFRESH, setItem, setSubmittedProfit, setUserRoleId
} from 'core/Auth/utils';
import { CalenderModel } from 'Models/CalenderModel';
import * as Animatable from "react-native-animatable";
import { DashboardModel } from 'Models/DashboardModel';
import { LeadModel } from 'Models/LeadModel';
import { LeadModelNext } from 'Models/LeadModelNext';
import { OpportunitiesModel } from 'Models/OpportunitiesModel';
import { OpportunitiesNextModel } from 'Models/OpportunitiesNextModel';
import { PriceApprovalModel } from 'Models/PriceApprovalModel';
import { upcomingSubmittionModel } from 'Models/UpcomingSubmittionModel';
import moment from 'moment';
import 'moment-timezone'
import React, { useEffect, useState } from 'react';
//import {API_URL} from '@env';
import {
  ActivityIndicator, Dimensions,
  Image,
  LogBox, SafeAreaView,
  StatusBar,
  StyleSheet
} from 'react-native';
import DropDownPicker from 'react-native-dropdown-picker';
import { FlatList, ScrollView } from 'react-native-gesture-handler';
import { Screen, showErrorMessage, View } from 'ui';
import CalenderCard from 'ui/CalenderCard';
import { LoseIcon } from 'ui/icons/LoseIcon';
import { SubmittedIcon } from 'ui/icons/SubmittedIcon';
import { WonIcon } from 'ui/icons/WonIcon';
import MonthsRender from 'ui/MonthsRender';
import OpportunitiyCards from 'ui/OpportunitiyCards';
import PriceApprovalCard from 'ui/PriceApprovalCard';
import RenderLeadTitle from 'ui/RenderLeadTitle';
import RenderOpportunitiyTitle from 'ui/RenderOpportunitiyTitle';
import AppStatusBar from 'ui/theme/AppStatusBar';
import ToAssignCard from 'ui/ToAssignCard';
import CalenderCardLoading from 'ui/CalenderCardLoading';
import EvaluateCards from 'ui/EvaluateCards';
import { EvaluateDataModel } from 'Models/EvaluateDataModel';
import { jwtModel } from 'Models/jwtModel';
import { signOut } from 'core';
import { Card, Text } from '@ui-kitten/components';




export const Home = props => {
  function getCurrenDate()
  {
    const timezoneOffset = moment()
    
    console.log(timezoneOffset.tz('Asia/Dubai'));

    // Create a new Date object for the current date and time
return timezoneOffset
 
  }
  function valid()
  {
    getCurrenDate()
    console.log(getAcessToken())
    var decoded = jwt_decode(getAcessToken()) as jwtModel;
    var expiry = moment.unix(decoded.exp)
    var expiry2 = moment(expiry).local()
    var initiation = moment.unix(decoded.iat)
    var initiation2 = moment(initiation)

    
    if (getCurrenDate() != expiry) {
      
      if (getCurrenDate() < expiry) {
        
        return true
      }
      else {
        
        return false
      }
    }
    else {

      return false
    }
  }

function refreshApi() {
  dashboardTask().then(dashboardStatus => {
    dashboardData = dashboardStatus;
    setSubmittedProfitValue(dashboardData.items[0].profit.replace(' ', '').trim());
    setSubmittedValue(dashboardData.items[0].value.replace(' ', '').trim());
    setWonProfit(dashboardData.items[1].profit.replace(' ', '').trim());
    setWonValue(dashboardData.items[1].value.replace(' ', '').trim());
    setLoseProfit(dashboardData.items[2].profit.replace(' ', '').trim());
    setLoseValue(dashboardData.items[2].value.replace(' ', '').trim());
  });
  
}
checkValidiy()
function checkValidiy(){
  if (valid()) {
    
    dashboardTask().then(dashboardStatus => {
      dashboardData = dashboardStatus;
      setSubmittedProfitValue(dashboardData.items[0].profit.replace(' ', '').trim());
      setSubmittedValue(dashboardData.items[0].value.replace(' ', '').trim());
      setWonProfit(dashboardData.items[1].profit.replace(' ', '').trim());
      setWonValue(dashboardData.items[1].value.replace(' ', '').trim());
      setLoseProfit(dashboardData.items[2].profit.replace(' ', '').trim());
      setLoseValue(dashboardData.items[2].value.replace(' ', '').trim());
    })
  }
  else {

    refreshToken().then(status => {
      if (status) {
        
        refreshApi()
      }
      else {
        signOut()
      }
    })
  }
    //Get new Token or LogOut
  }
  var dashboardData: DashboardModel = new DashboardModel();
  setItem(LOGGEDIN, true);
  const [calenderDataState, SetCalenderDataState] = useState([]);
  const [opportunitiesDataState, SetOpportunitiesDataState] = useState([]);
  const [evaluateDataState, setEvaluateDataState] = useState([]);

  const [leadDataState, SetLeadDataState] = useState([]);
  const [leadFullDataState, SetLeadFullDataState] = useState([]);
  const [opportunitiesLoading, setOpportunitiesLoading] = useState(true);
  const [evaluateLoading, setEvaluateLoading] = useState(true);

  const [leadLoading, setLeadLoading] = useState(true);


  const [calenderLoading, setCalenderLoading] = useState(true);
  const [calenderQueryYear, setCalenderQueryYear] = useState(
    new Date().getFullYear(),
  );
  const [calenderQueryMonth, setCalenderQueryMonth] = useState(
    new Date().getMonth().valueOf()+1,
  );
  const calender = async calenderDate => {
    var calenderData: CalenderModel;
    SetCalenderDataState([]);
    setCalenderLoading(true);
    await client
      .get("/telescope/calendar/", {
        headers: {
          Authorization: "Bearer " + getToken().access_token,
          app_user: getUserName(),
          user_role_id: getUserRoleId(),
          token_key: getUserToken(),

          "content-type": "application/x-www-form-urlencoded",
          sub_date: calenderDate,
        },
      })
      .then(function (response) {
        calenderData = JSON.parse(
          JSON.stringify(response.data),
        ) as CalenderModel;

        setTimeout(() => {
          setCalenderLoading(false);
          SetCalenderDataState(calenderData.items);

        }, 500);
        return calenderData;

      })
      .catch(function (error) {
  
        return error.status;
        
      });

     
  
  };
  var [submittiedValue,setSubmittedValue] = useState('N/A');
  var [submittiedProfitValue,setSubmittedProfitValue] = useState('N/A');
  var [wonProfit,setWonProfit] = useState('N/A');
  var [wonValue,setWonValue] = useState('N/A');
  var [loseProfit,setLoseProfit] = useState('N/A');
  var [lostValu,setLoseValue] = useState('N/A');
  var [refresh, setRefresh] = useState(true);

  
  useEffect(() => {
    checkValidiy()

    if(getUserRoleId() == 822)
    {
      setToDoState(true);
    }
    else if(getUserRoleId() == 12)
    {
      setToDoState(true);
    }
    else if(getUserRoleId() ==  762)
    {
      setToDoState(true);
    }
    else
    {
      setToDoState(false);
    }
 
    evaluate()
    
    props.navigation.addListener('focus', () => {      
      if (getREFRESH()) {
        checkValidiy()

        if (calenderQueryMonth >= 10) {
          calender(calenderQueryMonth + '/' + calenderQueryYear);
        } else {
          calender('0' + calenderQueryMonth + '/' + calenderQueryYear);
        }
        if (getSelectedLI() === 'To Assign') {
          console.log(selectedLeadItems);
          
          leadToAssign();

        }
        else if (getSelectedLI() === 'Price Approval') {
          waitingPriceApproval();
        }
        else if (getSelectedLI() === 'Upcoming') {
          upcomingSubmittion();
        }
        if (getSelectedOP() === 'All') {
          console.log(selectedLeadItems);
          
          allOpportunities();

        }
        else if (getSelectedOP() === 'Opportunities') {
          allOpportunitiesWithFilter(10);
        }
        else if (getSelectedOP() === 'Lead') {
          allOpportunitiesWithFilter(9);
        }
        dashboardTask().then(dashboardStatus => {
          dashboardData = dashboardStatus;
          setSubmittedProfitValue(dashboardData.items[0].profit.replace(' ', '').trim());
          setSubmittedValue(dashboardData.items[0].value.replace(' ', '').trim());
          setWonProfit(dashboardData.items[1].profit.replace(' ', '').trim());
          setWonValue(dashboardData.items[1].value.replace(' ', '').trim());
          setLoseProfit(dashboardData.items[2].profit.replace(' ', '').trim());
          setLoseValue(dashboardData.items[2].value.replace(' ', '').trim());
        });
        setRefresh(false)
      }
   
    });
    LogBox.ignoreLogs(['VirtualizedLists should never be nested']);
  }, [calenderQueryMonth, calenderQueryYear, props.navigation, getUserRoleId]);
  

  const leadToAssign = async () => {
    SetLeadDataState([]);
    setLeadNext('');
    setLeadLoading(true);
    var leadData: LeadModel;

    await client
      .get('/telescope/all_to_assign/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          user_role_id: getUserRoleId(),
          token_key: getUserToken(),
          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        leadData = JSON.parse(JSON.stringify(response.data)) as LeadModel;
        SetLeadDataState(leadData.items);
        setLeadNext(leadData.next.$ref);
        setLeadLoading(false);
        console.log("IS lead still loading?????",leadLoading);
        
        
        return leadData;
      })
      .catch(function (error) {
     
          console.log('Login error response is: ', error.message);
          return error.status;
        });
  };
  const upcomingSubmittion = async () => {
    SetLeadDataState([]);
    setLeadNext('');
    setLeadLoading(true);
    var upComingData: upcomingSubmittionModel;

    await client
      .get('/telescope/upcoming_submission/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          user_role_id: getUserRoleId(),
          token_key: getUserToken(),

          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        setLeadLoading(false);
        upComingData = JSON.parse(JSON.stringify(response.data)) as LeadModel;
        SetLeadDataState(upComingData.items);
        setLeadNext(upComingData.next.$ref);
        return upComingData;
      })
      .catch(function (error) {
        console.log('Login error response is: ', error.message);
        return error.status;
      });
  };
  const allOpportunities = async () => {
    setOpportunitiesLoading(true)
    var opportunitiesData: OpportunitiesModel;
     await client
      .get('/telescope/deals/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          user_role_id: getUserRoleId(),
          token_key: getUserToken(),

          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
       .then(function (response) {
        opportunitiesData = JSON.parse(
          JSON.stringify(response.data)
        ) as OpportunitiesModel;
        SetOpportunitiesDataState(opportunitiesData.items);
        setOpportunitiesLoading(false);
        setNext(opportunitiesData.next.$ref);
        return opportunitiesData;
      })
      .catch(function (error) {
        console.log('All Opportunities: ', error.response.status);
        return error.status;
      });
  };
  const evaluate = async () => {
    setEvaluateLoading(true)
    var evaluatesData: EvaluateDataModel;
     await client
      .get('/telescope/evaluate/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          token_key: getUserToken(),

          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        evaluatesData = JSON.parse(
          JSON.stringify(response.data)
        ) as EvaluateDataModel;
        setEvaluateDataState(evaluatesData.items);
        setEvaluateLoading(false);
        setNextEvaluate(evaluatesData.next.$ref);
        return evaluatesData;
      })
      .catch(function (error) {
        console.log('All Evaluates: ', error.message);
        return error.status;
      });
  };
  const waitingPriceApproval = async () => {
    var opportunitiesData: PriceApprovalModel;
    SetLeadDataState([]);
    setLeadNext('');
    await client
      .get('/telescope/waiting_price_approval/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          token_key: getUserToken(),

          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        opportunitiesData = JSON.parse(
          JSON.stringify(response.data),
        ) as PriceApprovalModel;
        SetLeadDataState(opportunitiesData.items);
        setLeadLoading(false);
        setNextPriceApproval(opportunitiesData.next.$ref);
        return opportunitiesData;
      })
      .catch(function (error) {
        console.log('Waiting Price Approval: ', error.message);
        return error.status;
      });
  };
  var [next, setNext] = useState('');
  var [nextEvaluate, setNextEvaluate] = useState('');

  var [nextPriceApproval, setNextPriceApproval] = useState('');

  var [prev, setPrev] = useState('');
  var [leadNext, setLeadNext] = useState('');
  var [leadPrev, setLeadPrev] = useState('');

  const leadToAssignNext = async pageNumber => {
    var leadData: LeadModelNext;

     await client
      .get('/telescope/all_to_assign/?page=' + pageNumber, {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          'User-Agent': 'Mozilla/5.0',
          token_key: getUserToken(),

          user_role_id: getUserRoleId(),
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        leadData = JSON.parse(JSON.stringify(response.data)) as LeadModelNext;
        SetLeadDataState(leadData.items);
        SetLeadFullDataState(leadData.items);
        setLeadNext(leadData.next.$ref);
        setLeadPrev(leadData.prev.$ref);
        setLeadLoading(false);
        return leadData;
      })
      .catch(function (error) {
        console.log('Login error response is: ', error.message);
        return error.status;
      });
   
  };

  const allOpportunitiesNext = async pageNumber => {
    setOpportunitiesLoading(true)

    var opportunitiesData: OpportunitiesNextModel;
  await client
      .get('/telescope/deals/?page=' + pageNumber, {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          token_key: getUserToken(),

          user_role_id: getUserRoleId(),
          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
        },
      })
      .then(function (response) {
        opportunitiesData = JSON.parse(
          JSON.stringify(response.data),
        ) as OpportunitiesNextModel;
        SetOpportunitiesDataState(opportunitiesData.items);
        setNext(opportunitiesData.next.$ref);
        setPrev(opportunitiesData.prev.$ref);
        setOpportunitiesLoading(false);
        return opportunitiesData;
      })
      .catch(function (error) {
        console.log('Login error response is: ', error.data);
        return error.status;
      });
   
  };
  const allOpportunitiesWithFilter = async id => {
    setOpportunitiesLoading(true)

    var opportunitiesData: OpportunitiesModel;
    await client
      .get('/telescope/deals/', {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          token_key: getUserToken(),

          'User-Agent': 'Mozilla/5.0',
          'content-type': 'application/x-www-form-urlencoded',
          deal_type: id,
          user_role_id: getUserRoleId(),
        },
      })
      .then(function (response) {
        opportunitiesData = JSON.parse(
          JSON.stringify(response.data),
        ) as OpportunitiesModel;
        SetOpportunitiesDataState(opportunitiesData.items);
        setOpportunitiesLoading(false);
        setNext(opportunitiesData.next.$ref);
        return opportunitiesData;
      })
      .catch(function (error) {
        console.log('Login error response is: ', error.data);
        return error.status;
      });
  
  };
  const allOpportunitiesWithFilterNext = async (id, page = '0') => {
    setOpportunitiesLoading(true)

    var opportunitiesData: OpportunitiesNextModel;
    await client
      .get('/telescope/deals/?page=' + page, {
        headers: {
          Authorization: 'Bearer ' + getToken().access_token,
          app_user: getUserName(),
          'User-Agent': 'Mozilla/5.0',
          token_key: getUserToken(),

          'content-type': 'application/x-www-form-urlencoded',
          deal_type: id,
          user_role_id: getUserRoleId(),
        },
      })
      .then(function (response) {
        opportunitiesData = JSON.parse(
          JSON.stringify(response.data),
        ) as OpportunitiesNextModel;
        SetOpportunitiesDataState(opportunitiesData.items);
        setOpportunitiesLoading(false);
        setNext(opportunitiesData.next.$ref);
        setPrev(opportunitiesData.prev.$ref);

        return opportunitiesData;
      })
      .catch(function (error) {
        console.log('Login error response is: ', error.data);
        return error.status;
      });
    
  };
  function recallCalender(year) {
    checkValidiy()
    setCalenderLoading(true);
    SetCalenderDataState([]);
    calender('0' + calenderQueryMonth + '/' + year);
  }
  console.log('selected month is:', calenderQueryMonth);

  const [selectedOpportunitiyItems, setSelectedOpportunitiesItems] =
    useState('All');
  const [selectedLeadItems, setSelectedLeadItems] = useState('To Assign');

  const [open, setOpen] = useState(false);
  const [items, setItems] = useState([
    {label: '2019', value: 2019},
    {label: '2020', value: 2020},
    {label: '2021', value: 2021},
    {label: '2022', value: 2022},
    {label: '2023', value: 2023},
    {label: '2024', value: 2024},
    {label: '2025', value: 2025},
    {label: '2026', value: 2026},
    {label: '2027', value: 2027},
    {label: '2028', value: 2028},
  ]);
  function nextPage() {
    var pageNumber = next.split('=');
    SetOpportunitiesDataState([]);
    setOpportunitiesLoading(true);
    if (getSelectedOP() === 'All') {
      console.log(selectedLeadItems);
      
      allOpportunities();

    }
    else if (getSelectedOP() === 'Opportunities') {
      allOpportunitiesWithFilter(10);
    }
    else if (getSelectedOP() === 'Lead') {
      allOpportunitiesWithFilter(9);
    }
  }
  function nextLeadPage() {
    var pageNumber = leadNext.split('=');
    SetLeadDataState([]);
    setLeadLoading(true);
    console.log(pageNumber[1]);

    if (getSelectedLI() === 'To Assign') {
      console.log(selectedLeadItems);
      
      leadToAssign();

    }
    else if (getSelectedLI() === 'Price Approval') {
      waitingPriceApproval();
    }
    else if (getSelectedLI() === 'Upcoming') {
      upcomingSubmittion();
    }
  }
  function prevLeadPage() {
    var pageNumber = leadNext.split('=');
    if (pageNumber !== undefined) {
      SetLeadDataState([]);
      setLeadLoading(true);
      leadToAssignNext(pageNumber[1]);
    } else {
      setLeadPrev('');
      SetLeadDataState([]);
      setLeadLoading(true);
      leadToAssign();
    }
  }
  function prevPage() {
    var pageNumber = prev.split('=');
    SetOpportunitiesDataState([]);
    setOpportunitiesLoading(true);
    if (pageNumber[1] !== undefined) {
      if (selectedOpportunitiyItems === 'All') {
        allOpportunitiesNext(pageNumber[1]);
      } else if (selectedOpportunitiyItems === 'Opportunities') {
        allOpportunitiesWithFilterNext(9, pageNumber[1]);
      } else if (selectedOpportunitiyItems === 'Lead') {
        allOpportunitiesWithFilterNext(10, pageNumber[1]);
      }
    } else {
      setPrev('');
      if (selectedOpportunitiyItems === 'All') {
        allOpportunities();
      } else if (selectedOpportunitiyItems === 'Opportunities') {
        allOpportunitiesWithFilter(9);
      } else if (selectedOpportunitiyItems === 'Lead') {
        allOpportunitiesWithFilter(10);
      }
    }
  }

  const [openUserRole, setOpenUserRole] = useState(false);
  var firstName = getName().split(' ')

  const filteredUserRoles = getUserRoles()?.map(x => {
    return {
      label: x.description,
      value: x.role_value,
    };
  });
  const [itemsUserRole, setItemsUserRole] = useState(filteredUserRoles);
  const [valueUserRole, setValueUserRole] = useState(null);
  const [toDoState, setToDoState] = useState(false);
  const [evaluateState , setEvaluateState] = useState(false);


  function changeUserRole() {
    checkValidiy()
    //debug here
    setUserRoleId(valueUserRole!);
    console.log('Selected user role id = '+ valueUserRole);
    
    if(getUserRoleId() == 822)
    {
      console.log('To DO TRUE 822');
      setToDoState(true);
    }
    else if(getUserRoleId() == 12)
    {
      console.log('To DO TREU 12 ');
      setToDoState(true);
    }
    else if(getUserRoleId() ==  762)
    {
      console.log('To DO TREU 762 ');
      setToDoState(true);
    }
    else
    {
      console.log('To DO False');
      setToDoState(false);
    }
    if (getSelectedOP() === 'All') {
      console.log(selectedLeadItems);
      
      allOpportunities();

    }
    else if (getSelectedOP() === 'Opportunities') {
      allOpportunitiesWithFilter(10);
    }
    else if (getSelectedOP() === 'Lead') {
      allOpportunitiesWithFilter(9);
    }
    if (calenderQueryMonth >= 10) {
      calender(calenderQueryMonth + '/' + calenderQueryYear);
    } else {
      calender('0' + calenderQueryMonth + '/' + calenderQueryYear);
    }
    if(getSelectedLI() === 'To Assign')
    {
      
      leadToAssign();

    }
    else if (getSelectedLI() === 'Price Approval')
    {
      waitingPriceApproval();
    }
    else if(getSelectedLI() === 'Upcoming')
    {
      upcomingSubmittion();
    }
    dashboardTask().then(dashboardStatus => {
      dashboardData = dashboardStatus;
      if(dashboardData.items.length<1)
      {
        setSubmittedProfit('N/A');
        setSubmittedValue('N/A');
        setWonProfit('N/A');
        setWonValue('N/A');
        setLoseProfit('N/A');
        setLoseValue('N/A');
      }
      else
      {
      var submittedProfit = dashboardData.items[0].profit.replace(' ', '').trim();
      var submittedValue = dashboardData.items[0].value.replace(' ', '').trim();
      var wonProfit = dashboardData.items[1].profit.replace(' ', '').trim();
      var wonValue = dashboardData.items[1].value.replace(' ', '').trim();
      var loseProfit = dashboardData.items[2].profit.replace(' ', '').trim();
      var loseValue = dashboardData.items[2].value.replace(' ', '').trim();
      setSubmittedProfit(submittedProfit);
      setSubmittedValue(submittedValue);
      setWonProfit(wonProfit);
      setWonValue(wonValue);
      setLoseProfit(loseProfit);
      setLoseValue(loseValue);
      }
    });
  }
  const RenderHeader = () => {
    const profileName = getName();
    const initials = profileName
      .split(" ")
      .map((name) => name.charAt(0))
      .join("");
    return (
      <View
        style={headerStyles.container}
        level="1"
      >
        <View style={headerStyles.rowContainer}>
          <TouchableOpacity>
            <View style={headerStyles.roundedProfile}>
              <Text style={headerStyles.initials} category="h1">
                {initials}
              </Text>
            </View>
          </TouchableOpacity>
          <View style={headerStyles.nameContainer}>
            <Text style={headerStyles.name} category="h6">
              {profileName}
            </Text>
          </View>
          <TouchableOpacity style={headerStyles.menuButton}>
          </TouchableOpacity>
        </View>
      </View>
    );
  };
  
  

  return (
    <Screen>
      <View flex={1}>
        <ScrollView  showsVerticalScrollIndicator={false} stickyHeaderIndices={[0]}>
        <RenderHeader/>   
        <View  flexDirection={'column'}
              flex={0}
              height={50}
              backgroundColor={'profileBackground'}
            >
              <DropDownPicker
                     placeholderStyle={styles.placeHolder}
                     dropDownContainerStyle={styles.dropDown}
                     placeholder={getUserRole()}
                     open={openUserRole}
              value={valueUserRole}
              modalTitle={'User Roles'}
              textStyle={{
                fontSize: 16,
                
                color: '#333',
              }}
              listItemContainerStyle={{  paddingVertical: 12,
                paddingHorizontal: 16,
                borderBottomWidth: 1,
                
                borderBottomColor: '#ccc',}}
              selectedItemLabelStyle={{ fontSize: 16,
                color: '#757575' }}
              selectedItemContainerStyle={{flexDirection: 'row',
                justifyContent: 'space-between',
                backgroundColor: '#e6f7ff',

              alignItems: 'center',
              paddingHorizontal: 10,
              paddingVertical: 8, }}

              modalProps={{
                animationType: "fade"
              }}
              items={itemsUserRole}
              
                     setOpen={setOpenUserRole}
                     setValue={setValueUserRole}
              onChangeValue={changeUserRole}
              
                     listMode="MODAL"
                     setItems={setItemsUserRole}
                     style={{
                       borderWidth: 0,         
                       marginStart:10,
                       backgroundColor:'#365273',
                       height: 10,
                       width: Dimensions.get('window').width / 2.5,
                     }}
                   />
             
        </View>
          <View flexDirection={'column'}>
            <View >
            <View style={styles.profileView} position={'relative'}/>
        
            <View
              flexDirection={'row'}
              position={'absolute'}
              alignContent={'center'}
              flex={1}
                justifyContent={'center'}>
                 <View style={cardStyles.container}>
               <Card style={cardStyles.card}>
        <View style={cardStyles.iconContainer}>
          <SubmittedIcon/>
        </View>
        <Text category='h3' style={cardStyles.title}>
          Profit
        </Text>
        <Text category='h2' style={cardStyles.value}>
          {submittiedValue}
        </Text>
      </Card>
      <Card style={cardStyles.woncard}>
        <View style={cardStyles.iconContainer}>
          <WonIcon/>
        </View>
        <Text category='h3' style={cardStyles.title}>
          Won
        </Text>
        <Text category='h2' style={cardStyles.value}>
          {wonValue}
        </Text>
      </Card>
      <Card style={cardStyles.losecard}>
        <View style={cardStyles.iconContainer}>
          <LoseIcon/>
        </View>
        <Text category='h3' style={cardStyles.title}>
          Lose
        </Text>
        <Text category='h2' style={cardStyles.value}>
          {lostValu}
        </Text>
      </Card>
                  </View>
            </View>
            </View>
          </View>
          <AppStatusBar backgroundColor={'#365273'} />
          <SafeAreaView style={styles.container}>
            <View flexDirection={'row'} marginTop={'xxl'} margin={'m'}>
              <View alignSelf={'center'} flex={1}>
                <Text style={{fontWeight:'bold' ,fontSize:20 ,color:'black'}}>
                  Calender
                </Text>
              </View>
              <View alignSelf={'flex-end'} flex={0}>
                <DropDownPicker
                  open={open}
                  value={calenderQueryYear}
                  items={items}
                  setOpen={setOpen}
                  setValue={setCalenderQueryYear}
                  onChangeValue={recallCalender}
                  placeholder={'2022'}
                  textStyle={{color: '#8D969A', fontWeight: 'bold'}}
        
                  dropDownContainerStyle={{
                    backgroundColor: '#FAFDFF',
                    borderWidth: 0,
                  }}
                  style={{
                    backgroundColor: '#FAFDFF',
                    borderWidth: 0,
                    zIndex:0,
                    position:'relative',
                    
                    width: 90,
                  }}
                  listMode={'MODAL'}
                  setItems={setItems}
                />
              </View>
            </View>
            <FlatList
              data={DATA}
              showsHorizontalScrollIndicator={false}
              initialScrollIndex={calenderQueryMonth-1}
              getItemLayout={(data, index) => ({
                length: 50,
                height: 50,
                offset: 50 * index,
                index,
              })}
              renderItem={({item}) => (
                <MonthsRender
                  title={item.title}
                  itemId={item.id}
                  items={DATA}
                  calenderQueryMonth={calenderQueryMonth}
                  setCalenderQueryMonth={setCalenderQueryMonth}
                  selectedYear={calenderQueryYear}
                  reCallCalender={calender}
                  setcalenderItems={SetCalenderDataState}
                  calenderisLoading={setCalenderLoading}
                />
              )}
              keyExtractor={item => item.id}
              horizontal={true}
            />
            <View flex={1} alignItems={'center'} marginTop={'xl'} marginBottom={'xl'}>
              {calenderLoading && <CalenderCardLoading calenderLoading={calenderLoading}/>}
              {calenderDataState.length < 1 && !calenderLoading && (
                <Text>No Events</Text>
              )}
              <FlatList
                data={calenderDataState}
                showsHorizontalScrollIndicator={false}
                renderItem={({item}) => (
                  <CalenderCard
                    customer_e_name={item.customer_e_name}
                    deal_description={item.deal_description}
                    assigned_to={item.assigned_to}
                    status={item.status}
                    submission_date={item.submission_date}
                    id={item.id}
                    calenderStyle={item.style}
                    navigation={props.navigation.navigate}
                  />
                )}
                keyExtractor={item => item.id as string}
                horizontal={true}
              />
            </View>
            {toDoState &&
            <View >
            <View flex={1} flexDirection={'row'} alignItems={'center'}>
              <Text
                    style={{
                      fontWeight: 'bold', fontSize: 20,
                      color: 'black', margin: 20,
                    }} >
                To Do's
              </Text>
              <View flex={1} alignItems={'flex-end'}>
                <TouchableOpacity
                  onPress={() =>
                    props.navigation.navigate('OpportunitiesAndLeadsView')
                  }>
                      <Text style={{
                        fontWeight: 'bold', fontSize: 18,
                        color: 'grey', margin: 20
                      }}>
                    See All
                  </Text>
                </TouchableOpacity>
              </View>
            </View>

            <View
              flex={1}
              padding={'m'}
              backgroundColor={'white'}
              margin={'s'}
              flexDirection={'column'}
              borderRadius={15}
              borderTopColor={'black'}
              shadowColor={'black'}
              shadowOffset={{width: 0, height: 2}}
              shadowOpacity={0.25}
              shadowRadius={3.84}
              elevation={5}>
              <View flexDirection={'row'} padding={'s'}>
                <RenderLeadTitle
                  title={'To Assign'}
                  setSelectedLeadItems={setSelectedLeadItems}
                  selectedLeadItems={selectedLeadItems}
                  priceApprovalData={waitingPriceApproval}
                  leadItemsData={leadToAssign}
                  upcomingSubmittionItems={upcomingSubmittion}

                />
                <RenderLeadTitle
                  title={'Price Approval'}
                  setSelectedLeadItems={setSelectedLeadItems}
                  selectedLeadItems={selectedLeadItems}
                  leadItems={leadToAssign }  
                  priceApprovalData={waitingPriceApproval}
                  upcomingSubmittionItems={upcomingSubmittion}


                />
                <RenderLeadTitle
                  title={'Upcoming'}
                  setSelectedLeadItems={setSelectedLeadItems}
                  selectedLeadItems={selectedLeadItems}
                  priceApprovalData={waitingPriceApproval}
                  leadItemsData={leadToAssign}
                  upcomingSubmittionItems={upcomingSubmittion}

                />
              </View>
              <View>
                {leadLoading && <ActivityIndicator color="#000" />}
                <FlatList
                  data={leadDataState}
                  showsHorizontalScrollIndicator={false}
                  scrollEnabled={false}
                  renderItem={({item}) => {
                  if(selectedLeadItems == 'To Assign')
                  {
                    return <ToAssignCard
                    setRefresh={setRefresh}
                      status={item.status}
                      customer_name={item.customer_name}
                      country={item.country}
                      submission_date={moment(item.submission_date).format(
                        'DD/MM/YYYY'
                      )}
                      deal_description={item.deal_description}
                      account_manager={item.account_manager}
                      id={item.id}
                      reference_no={item.reference_no}
                      deal_type={item.deal_type}
                      deal={item}

                      navigation={props} />;
                  }
                  else if(selectedLeadItems == 'Price Approval') { return <PriceApprovalCard
                    status={item.status}
                    customer_name={item.customer_name}
                    country={item.country}
                    submission_date={moment(item.submission_date).format(
                      'DD/MM/YYYY'
                    )}
                    deal_description={item.deal_description}
                    account_manager={item.account_manager}
                    id={item.id}
                    reference_no={item.reference_no}
                    deal_type={item.deal_type}
                    deal={item}
                    navigation={props} />; 
                  }
                  else if(selectedLeadItems == 'Upcoming') { return <PriceApprovalCard
                    status={item.status}
                    customer_name={item.customer_name}
                    country={item.country}
                    submission_date={moment(item.submission_date).format(
                      'DD/MM/YYYY'
                    )}
                    deal_description={item.deal_description}
                    account_manager={item.account_manager}
                    id={item.id}
                    reference_no={item.reference_no}
                    deal_type={item.deal_type}
                    deal={item}

                    navigation={props} />; 
                  }
                    
                  }}
                  keyExtractor={item => item.id as string}
                  horizontal={false}
                />
                <View
                  flex={1}
                  flexDirection={'row'}
                  alignItems={'center'}
                  alignContent={'center'}
                  alignSelf={'center'}>
                  {leadPrev.length > 1 && (
                    <>
                      <TouchableOpacity
                        style={{flex: 0, alignSelf: 'center'}}
                        onPress={prevLeadPage}>
                        <Text color={'blue'}>Prev Page</Text>
                      </TouchableOpacity>
                      <View width={20} />
                    </>
                  )}
                  {leadNext.length > 1 && (
                    <TouchableOpacity
                      style={{flex: 0, alignSelf: 'center'}}
                      onPress={nextLeadPage}>
                      <Text color={'blue'}>Next Page</Text>
                    </TouchableOpacity>
                  )}
                </View>
              </View>
            </View>
            </View>
}
          <View flex={1} flexDirection={'row'} alignItems={'center'}>
              <Text
               style={{
                fontWeight: 'bold', fontSize: 20,
                color: 'black', margin: 20,
              }}>
                Opportunities and Leads
              </Text>
              <View flex={1} alignItems={'flex-end'}>
                <TouchableOpacity
                  onPress={() =>
                    props.navigation.navigate('OpportunitiesAndLeadsView')
                  }>
                  <Text style={{
                      fontWeight: 'bold', fontSize: 18,
                      color: 'grey', margin: 20,
                    }}>
                    See All
                  </Text>
                </TouchableOpacity>
              </View>
            </View>

            <View
              flex={1}
              padding={'m'}
              backgroundColor={'white'}
              margin={'s'}
              flexDirection={'column'}
              borderRadius={15}
              borderTopColor={'black'}
              shadowColor={'black'}
              shadowOffset={{width: 0, height: 2}}
              shadowOpacity={0.25}
              shadowRadius={3.84}
              elevation={5}>
              <View flexDirection={'row'} padding={'s'}>
                <RenderOpportunitiyTitle
                  title={'All'}
                  data={SetOpportunitiesDataState}
                  setSelectedOpportunitiesItems={setSelectedOpportunitiesItems}
                  recall={allOpportunities}
                  selectedOpportunitiyItems={selectedOpportunitiyItems}
                />
                <RenderOpportunitiyTitle
                  title={'Opportunities'}
                  data={SetOpportunitiesDataState}
                  setSelectedOpportunitiesItems={setSelectedOpportunitiesItems}
                  recall={allOpportunitiesWithFilter}
                  selectedOpportunitiyItems={selectedOpportunitiyItems}
                />
                <RenderOpportunitiyTitle
                  title={'Lead'}
                  data={SetOpportunitiesDataState}
                  setSelectedOpportunitiesItems={setSelectedOpportunitiesItems}
                  recall={allOpportunitiesWithFilter}
                  selectedOpportunitiyItems={selectedOpportunitiyItems}
                />
              </View>
              <View  flexDirection={'column'}>
                {opportunitiesLoading && <ActivityIndicator color="#000" />}
                <OpportunitiyCards
                  opportunitiesDataState={opportunitiesDataState}
                  props={props.navigation}
                />
                <View
                  flex={1}
                  flexDirection={'row'}
                  
                  alignSelf={'center'}>
                  {prev.length > 1 && (
                    <>
                      <TouchableOpacity
                        style={{flex: 0, alignSelf: 'center'}}
                        onPress={prevPage}>
                        <Text color={'blue'}>Prev Page</Text>
                      </TouchableOpacity>
                      <View width={20} />
                    </>
                  )}
                  {next.length > 1 && (
                    <TouchableOpacity
                      style={{flex: 0, alignSelf: 'center', alignContent:'center',alignItems:'center'}}
                      onPress={nextPage}>
                      <Text color={'blue'} textAlign={'center'}>Next Page</Text>
                    </TouchableOpacity>
                  )}
                </View>
              </View>
            </View>
            <View flex={1} flexDirection={'row'} alignItems={'center'}>
              <Text
                fontWeight={'bold'}
                fontSize={20}
                color={'black'}
                margin={'m'}>
                Evaluate
              </Text>
              <View flex={1} alignItems={'flex-end'}>
                <TouchableOpacity
                  onPress={() =>
                   // props.navigation.navigate('OpportunitiesAndLeadsView') 
                    1
                  }>
                  <Text fontWeight={'900'} color={'textColo'} margin={'m'}>
                    See All
                  </Text>
                </TouchableOpacity>
              </View>
            </View>

            <View
              flex={1}
              padding={'m'}
              backgroundColor={'white'}
              margin={'s'}
              flexDirection={'column'}
              borderRadius={15}
              borderTopColor={'black'}
              shadowColor={'black'}
              shadowOffset={{width: 0, height: 2}}
              shadowOpacity={0.25}
              shadowRadius={3.84}
              elevation={5}>

              <View  flexDirection={'column'}>
                {evaluateLoading && <ActivityIndicator color="#000" />}
                <EvaluateCards
                  evaluateDataState={evaluateDataState}
                  props={props.navigation}
                />
                <View
                  flex={1}
                  flexDirection={'row'}
                  
                  alignSelf={'center'}>
                  {prev.length > 1 && (
                    <>
                      <TouchableOpacity
                        style={{flex: 0, alignSelf: 'center'}}
                        onPress={prevPage}>
                        <Text color={'blue'}>Prev Page</Text>
                      </TouchableOpacity>
                      <View width={20} />
                    </>
                  )}
                  {next.length > 1 && (
                    <TouchableOpacity
                      style={{flex: 0, alignSelf: 'center', alignContent:'center',alignItems:'center'}}
                      onPress={nextPage}>
                      <Text color={'blue'} textAlign={'center'}>Next Page</Text>
                    </TouchableOpacity>
                  )}
                </View>
              </View>
            </View>
          </SafeAreaView>
        </ScrollView>
      </View>
    </Screen>
  );
};

// eslint-disable-next-line no-lone-blocks

// <Button label="LogOut" onPress={signOut} />
// <Button
//   variant="secondary"
//   label="Show message"
//   onPress={() => showErrorMessage()}
// />
const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: StatusBar.currentHeight || 40,
    marginBottom: 40,
    paddingBottom: 10,
  },
  container2: {flex: 1},
  opportunitiesTabContainer: {
    flex: 1,
    height: 20,
  },
  opportunitiesTabContainerText: {
    flex: 1,
    textAlign: 'center',
    color: '#365273',
  },
  opportunitiesTabContainerTextTabbed: {
    flex: 1,
    textAlign: 'center',
    color: '#E08A67',
  },

  tabBarContainer: {
    flex: 1,
    flexDirection: 'row',
  },
  item: {
    marginVertical: 15,
  },
  profileView: {
    backgroundColor: '#365273',
    height: 80,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
   
    flex:0,
    flexDirection: 'column',
    borderBottomStartRadius: 30,
    borderBottomEndRadius: 30,
  },
  roundedProfile: {
    justifyContent: 'center', //Centered horizontally
    alignItems: 'center', //Centered vertically
    flex: 0,
    backgroundColor: 'white',
    width: 50,
    hadowColor: '#fff',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.35,
    shadowRadius: 3.84,

    elevation: 5,
    opacity: 1,
    height: 50,
    margin: 15,
    borderRadius: 25,
  },
  roundedCalender: {
    justifyContent: 'center', //Centered horizontally
    alignItems: 'center', //Centered vertically
    flex: 1,
    textAlign: 'center',
    alignContent: 'center',
    backgroundColor: '#E7F1F5',
    width: 50,
    height: 50,
    
    margin: 15,
    flexDirection: 'column',
    borderRadius: 25,
  },
  dropDown: {
    backgroundColor: '#365273',
    borderWidth: 0,
  },
  placeHolder: {
    color: '#ffffff',
    
  },
  roundedCalenderSelected: {
    justifyContent: 'center', //Centered horizontally
    alignItems: 'center', //Centered vertically
    flex: 1,
    textAlign: 'center',
    alignContent: 'center',
    backgroundColor: '#365273',
    width: 50,
    height: 50,
    
    margin: 15,
    flexDirection: 'column',
    borderRadius: 25,
  },
  calenderText: {
    color: 'white',
    fontWeight: '500',
  },
  calenderTextSelected: {
    color: '#365273',
    fontWeight: '500',
  },
  roundedMenuButton: {
    justifyContent: 'center', //Centered horizontally
    alignItems: 'center',
    alignSelf: 'flex-end', //Centered vertically
    flex: 0,
    backgroundColor: 'white',
    width: 50,
    opacity: 0.2,
    height: 50,
    
    margin: 15,
    borderRadius: 25,


    hadowColor: '#fff',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.45,
    shadowRadius: 4.84,

    elevation: 5,
  },
  tabBar: {
    flex: 1,
    backgroundColor: 'grey',
    borderRadius: 10,
    padding: 5,
    margin: 5,
    flexDirection: 'row',
    alignContent: 'center',
  },
  tab: {
    height: 50,
    flex: 1,
    margin: 2,
    borderTopStartRadius: 10,
    borderTopEndRadius: 10,

    backgroundColor: '#eab676',
    alignItems: 'center',
    justifyContent: 'center',
  },
  tabText: {
    color: 'white',
    fontSize: 10,
    fontWeight: 'bold',
  },
  submittedStyle: {
    backgroundColor: '#EDF5FF',
    width: Dimensions.get('window').width / 3.5,
    height: 130,
    borderRadius: 20,
    margin: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,
    opacity: 1,
  },
  wonStyle: {
    backgroundColor: '#EDFFFE',
    width: Dimensions.get('window').width / 3.5,
    height: 130,
    borderRadius: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,
    margin: 10,
  },
  loseStyle: {
    backgroundColor: '#FFFAF8',
    width: Dimensions.get('window').width / 3.5,
    height: 130,
    borderRadius: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,

    margin: 10,
  },
});
const cardStyles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  card: {
    alignItems: 'center',
    backgroundColor: '#EDF5FF',
    borderRadius: 20,
    marginTop: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    height:130,
    width: '32%',
  },
  woncard: {
    alignItems: 'center',
    backgroundColor: '#EDFFFE',
    borderRadius: 20,
    marginTop: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,
    height:130,
    width: '32%',
  },
  losecard: {
    alignItems: 'center',
    backgroundColor: '#FFFAF8',
    borderRadius: 20,
    marginTop: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,

    elevation: 5,
    height:130,
    width: '32%',
  },
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  modalBox: {
    backgroundColor: '#fff',
    borderRadius: 10,
    paddingVertical: 10,
    maxHeight: Dimensions.get('window').height * 0.7,
    elevation: 5,
  },
  modalItem: {
    paddingVertical: 10,
    borderBottomWidth: 2,
    borderBottomColor: '#000000',
  },
  modalItemText: {
    fontSize: 16,
    color: '#000',
  },
  iconContainer: {
    alignItems: 'center',
    alignSelf:'center',
    borderRadius: 50,
    height: 25,
    justifyContent: 'center',
    marginBottom: 16,
    width: 25,
  },
  icon: {
    height: 32,
    width: 32,
  },
  title: {
    color: '#4F4F4F',
    marginBottom: 8,
    fontSize:17,
    textAlign: 'center',
  },
  value: {
    color: '#2E3A59',
    fontWeight: 'bold',
    fontSize:10,

    textAlign: 'center',
  },
});
const DATA = [
  {
    id: 1,
    title: 'Jan',
  },
  {
    id: 2,
    title: 'Feb',
  },
  {
    id: 3,
    title: 'March',
  },
  {
    id: 4,
    title: 'Apr',
  },
  {
    id: 5,
    title: 'May',
  },
  {
    id: 6,
    title: 'June',
  },
  {
    id: 7,
    title: 'July',
  },
  {
    id: 8,
    title: 'Aug',
  },
  {
    id: 9,
    title: 'Sep',
  },
  {
    id: 10,
    title: 'Oct',
  },
  {
    id: 11,
    title: 'Nov',
  },
  {
    id: 12,
    title: 'Dec',
  },
];



const headerStyles = StyleSheet.create({
  container: {
    paddingTop: 40,
    paddingBottom: 20,
    paddingHorizontal: 20,
    backgroundColor: '#365273',
  },
  rowContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  roundedProfile: {
    backgroundColor: '#E4E9F2',
    borderRadius: 100,
    height: 70,
    width: 70,
    justifyContent: 'center',
    alignItems: 'center',
  },
  initials: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: 20,
  },
  nameContainer: {
    marginLeft: 20,
    flex: 1,
  },
  name: {
    color: 'white',
    fontWeight: 'bold',
  },
  menuButton: {
    height: 30,
    width: 30,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
