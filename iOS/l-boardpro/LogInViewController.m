//
//  LogInViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 27/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "LogInViewController.h"
#import "SingletonClass.h"
#import  <sqlite3.h>

@interface LogInViewController (){
    sqlite3 * database;
    UIActivityIndicatorView * activityIndicator;
}

@end

@implementation LogInViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(deleteSelectedUserFromDB) name:@"deleteRowofSelectedUser" object:nil];
    
    windowSize=[UIScreen mainScreen].bounds.size;
    // create header here.
    self.headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, windowSize.width, 55)];
    
    self.headerView.backgroundColor = [UIColor colorWithRed:55.0f/255.0f green:105.0f/255.0f blue:147.0f/255.0f alpha:1.0f];
    
    [self.view addSubview:self.headerView];
    
    
    
    
    self.headerView.layer.shadowRadius = 5.0;
    self.headerView.layer.shadowColor = [UIColor blackColor].CGColor;
    self.headerView.layer.shadowOpacity = 0.6;
    self.headerView.layer.shadowOffset = CGSizeMake(0.0f,5.0f);
    self.headerView.layer.shadowPath = [UIBezierPath bezierPathWithRect:self.headerView.bounds].CGPath;
    
    
    UILabel * headerTitle=[[UILabel alloc]initWithFrame:CGRectMake(60, 20, windowSize.width-120, 25)];
    headerTitle.text=@"LinkedIn";
    headerTitle.textAlignment=NSTextAlignmentCenter;
    headerTitle.font=[UIFont boldSystemFontOfSize:20];
    headerTitle.textColor=[UIColor whiteColor];
    [self.headerView addSubview:headerTitle];
    
    UIButton * cancelButton=[UIButton buttonWithType:UIButtonTypeCustom];
    cancelButton.frame=CGRectMake(15, 20, 50, 25);
    cancelButton.layer.cornerRadius=5;
    cancelButton.clipsToBounds=YES;
    [cancelButton setTitle:@"Back" forState:UIControlStateNormal];
    [cancelButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelButton addTarget:self action:@selector(cancelButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.headerView addSubview:cancelButton];
    
    self.view.backgroundColor=[UIColor whiteColor];
    
    // Adding activity indicator
    
    activityIndicator=[[UIActivityIndicatorView alloc]initWithFrame:CGRectMake(windowSize.width/2-20, windowSize.height/2-40, 40, 40)];
    activityIndicator.color=[UIColor blackColor];
    activityIndicator.alpha=1.0;
    activityIndicator.activityIndicatorViewStyle=UIActivityIndicatorViewStyleWhiteLarge;
    [self.view addSubview: activityIndicator];
    [self.view bringSubviewToFront:activityIndicator];
    
    
    [self createUI];
    // Do any additional setup after loading the view from its nib.
}

-(void)createUI{
    
  
    
    NSString * redirectURI=@"http://www.xxxxxxxx.com/";
    //load the the secret data from an uncommitted  LIALinkedInClientExampleCredentials.h file
    NSString *clientId = @"xxxxxxxxxxxxx"; //the client secret you get from the registered LinkedIn application
    NSString *clientSecret = @"xxxxxxxxxxxxxx"; //the client secret you get from the registered LinkedIn application
    NSString *state = @"xxxxxxxxxxxxxxxxxx"; //A long unique string value of your choice that is hard to guess. Used to prevent CSRF
    
    NSString * url=[NSString stringWithFormat:@"https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=%@&redirect_uri=%@&state=%@&scope=r_fullprofile+w_share&format=json",clientId,redirectURI,state];
    
    NSURL * instagramUrl=[NSURL URLWithString:url];
    NSURLRequest *req = [[NSURLRequest alloc]initWithURL:instagramUrl];
    webView=[[UIWebView alloc]initWithFrame:CGRectMake(0, 55,windowSize.width,windowSize.height)];
    webView.delegate=self;
    [ webView loadRequest:req];
    [self.view addSubview: webView];

}

#pragma mark- delegate method of webView

-(BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType{
    NSString * urlStr=[[request URL]absoluteString];
    
    NSString *orignalUrl =[request.URL absoluteString];
    NSArray* parts = [orignalUrl componentsSeparatedByString: @"="];
    if (parts.count>1&&[urlStr rangeOfString:@"code="].location!=NSNotFound) {
        NSLog(@"WebView URL %@",urlStr);
        
        NSString *  request_token = [parts objectAtIndex: 1];
        
        [[NSUserDefaults standardUserDefaults]                                                                                                                                               setObject:request_token forKey:@"code"];
        [[NSUserDefaults standardUserDefaults]synchronize];
        
        [self getAccessToken];
        
        return NO;
    }
    return YES;
}

-(void)getAccessToken{
    
    NSError * error=nil;
    NSURLResponse * urlResponse=nil;
    
    NSArray *grantedAccess = @[@"r_fullprofile", @"r_network"];
    
    NSString * redirectURI=@"http://www.globussoft.com/";
    //load the the secret data from an uncommitted  LIALinkedInClientExampleCredentials.h file
    NSString *clientId = @"77b801uof9g6lp"; //the client secret you get from the registered LinkedIn application
    NSString *clientSecret = @"Q6e5BF7sL8SOr2Gf"; //the client secret you get from the registered LinkedIn application
    NSString *state = @"DCEEFWF45453sdffef424"; //A long unique string value of your choice that is hard to guess. Used to prevent CSRF
    
    
    
    
    NSString * code=[[ NSUserDefaults standardUserDefaults]                                                                                                                                               valueForKey:@"code"];
    
    
    
    
    NSURL * postUrl=[NSURL URLWithString:[NSString stringWithFormat:@"https://www.linkedin.com/uas/oauth2/accessToken"]];
    
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:postUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    
    [request setHTTPMethod:@"POST"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSString * body=[NSString stringWithFormat:@"grant_type=authorization_code&code=%@&redirect_uri=%@&client_id=%@&client_secret=%@",code,redirectURI,clientId,clientSecret];
    
    [request setHTTPBody:[body dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES]];
    
    
    NSData  * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    
    id dictResponse = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:nil];
    NSLog(@"Response : %@",dictResponse);
    
    
    [[NSUserDefaults standardUserDefaults]setObject:[dictResponse objectForKey:@"access_token"] forKey:@"access_token"];
    [[NSUserDefaults standardUserDefaults]synchronize];
    
    
      [self fetchLinkedInData];
    
    [self dismissViewControllerAnimated:YES completion:nil];
    
  

    [[NSNotificationCenter defaultCenter]postNotificationName:@"loginCheckComplete" object:nil userInfo:nil];
    
}

#pragma mark-

-(void)fetchLinkedInData{
    
    [SingletonClass shareSinglton].education=[[NSMutableArray alloc]init];
    [SingletonClass shareSinglton].skills=[[NSMutableArray alloc]init];
    [[SingletonClass shareSinglton].education removeAllObjects];
    [[SingletonClass shareSinglton].skills removeAllObjects];
    
    NSString * accessToken= [[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSURL * getUrl=[NSURL URLWithString:[NSString stringWithFormat:@"https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,skills,educations,languages,twitter-accounts,picture-url)?oauth2_access_token=%@&format=json",accessToken]];
    
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:getUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"GET"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
    NSLog(@" response data %@",response);
    
    NSMutableDictionary * eduDict=[NSMutableDictionary dictionary];
    NSMutableDictionary * skillDict=[NSMutableDictionary dictionary];
    NSMutableDictionary * dict=[NSMutableDictionary dictionary];
    NSMutableDictionary * skillsDict=[NSMutableDictionary dictionary];
    
     NSMutableDictionary * Dict=[NSMutableDictionary dictionary];
   
    eduDict=[response objectForKey:@"educations"];
    NSArray * valuesArr=[eduDict objectForKey:@"values"];
    
    for (int i=0; i<valuesArr.count; i++) {
        dict=[valuesArr objectAtIndex:i];
        [[SingletonClass shareSinglton].education addObject:[dict objectForKey:@"schoolName"]];
    }
    
    skillsDict=[response objectForKey:@"skills"];
    NSArray * skillValuesArr=[skillsDict objectForKey:@"values"];
    for (int j=0; j<skillValuesArr.count; j++) {
        dict=[skillValuesArr objectAtIndex:j];
        skillDict=[dict objectForKey:@"skill"];
        [[SingletonClass shareSinglton].skills addObject:[skillDict objectForKey:@"name"]];
    }
    
    [SingletonClass shareSinglton].profileImgStr =[response objectForKey:@"pictureUrl"];
    [SingletonClass shareSinglton].userName=[[NSMutableString alloc]init];
    [[SingletonClass shareSinglton].userName appendFormat:[NSString stringWithFormat:@"%@",[response objectForKey:@"firstName"]]];
    
    [[SingletonClass shareSinglton].userName appendFormat:[NSString stringWithFormat:@" %@",[response objectForKey:@"lastName"]]];
    [SingletonClass shareSinglton].headLine=[response objectForKey:@"headline"];
     [SingletonClass shareSinglton].userId=[response objectForKey:@"id"];
    
    [self createSqliteTable];
    
}



#pragma mark- create sqLite table
// create sqlite table managing new accounts
-(void)createSqliteTable{
    
    NSArray * path=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSString * documentoryPath=[path objectAtIndex:0];
    NSString *databasePath = [documentoryPath stringByAppendingPathComponent:@"board11.sqlite"];
    NSFileManager *mgr=[NSFileManager defaultManager];
    
    if([mgr fileExistsAtPath:databasePath]==NO)
    {
        
        if (sqlite3_open([databasePath UTF8String], &database)==SQLITE_OK) {
            
            char * errormsg;
            const char *sqlStatement = "CREATE TABLE  LinkBoard (ID INTEGER PRIMARY KEY AUTOINCREMENT,UserId TEXT, UserFullName TEXT, ProfilePic TEXT,AccessToken TEXT)";
            
            if (sqlite3_exec(database, sqlStatement, NULL, NULL, &errormsg)!=SQLITE_OK) {
                NSLog(@"Failed to create table");
            }
            NSLog(@"Data base created");
        }
        
        sqlite3_close(database);
    }
    
    [self insertIntoTable];
    
}


#pragma mark- insert into table

-(void)insertIntoTable{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"board11.sqlite"];
    
    sqlite3_stmt *statement;
    
    
    
    if(sqlite3_open([databasePath UTF8String], &database)==SQLITE_OK)
    {
        NSString *querySQL=[NSString stringWithFormat:@"select UserFullName from LinkBoard where UserId=\"%@\"",[SingletonClass shareSinglton].userId];
        
        
        const char *query_stmt=[querySQL UTF8String];
        NSLog(@"QuerySQL in signIN :%@",querySQL);
        
        
        if (sqlite3_prepare_v2(database, query_stmt, -1, &statement, NULL)!=SQLITE_OK) {
            NSLog(@"Error %s",sqlite3_errmsg(database));
            return;
        }

        if(sqlite3_step(statement)==SQLITE_ROW)
        {
            UIAlertView * alertview=[[UIAlertView alloc]initWithTitle:nil message:@"USername is already  exist" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"Ok", nil];
            [alertview show];
        }
        
        else
        {
            NSString * access_token=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
            
            NSString *insertSQL=[NSString stringWithFormat:@"INSERT INTO LinkBoard (UserId ,UserFullName ,ProfilePic ,AccessToken) values(\"%@\",\"%@\",\"%@\",\"%@\")",[SingletonClass shareSinglton].userId,[SingletonClass shareSinglton].userName,[SingletonClass shareSinglton].profileImgStr,access_token];
            
            
            const char *insert_stmt=[insertSQL UTF8String];
            if (sqlite3_open([databasePath UTF8String], &database)!=SQLITE_OK) {
                NSLog(@"Error to Open");
                return;
            }
            
            if (sqlite3_prepare_v2(database, insert_stmt, -1, &statement, NULL)!=SQLITE_OK) {
                return;
            }
            
            
            if(sqlite3_step(statement)==SQLITE_DONE)
            {
                NSLog(@"You added successfully");
                
                
            }
            
        }
    }
    sqlite3_finalize(statement);
    sqlite3_close(database);
    [self retreiveDataFromSqlite];
}

//Retreive all logged in users list
-(void)retreiveDataFromSqlite{
    
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"board11.sqlite"];
    NSString *query = [NSString stringWithFormat:@"select * from LinkBoard"];
    
    
    sqlite3_stmt *compiledStmt=nil;
    if(sqlite3_open([databasePath UTF8String], &database)!=SQLITE_OK)
        NSLog(@"error to open");
    {
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &compiledStmt, NULL)== SQLITE_OK)
        {
            NSLog(@"prepared");
            
            [[SingletonClass shareSinglton].allData removeAllObjects];
            while(sqlite3_step(compiledStmt)==SQLITE_ROW)
            {
                char *userid = (char *) sqlite3_column_text(compiledStmt,1);
                char *userfullname = (char *) sqlite3_column_text(compiledStmt,2);
                char *profilepic = (char *) sqlite3_column_text(compiledStmt,3);
                char * accesstoken=(char *)sqlite3_column_text(compiledStmt,4);
                
                NSString *userId= [NSString  stringWithUTF8String:userid];
                
                NSString *userFullName  = [NSString stringWithUTF8String:userfullname];
                NSString *profilePic  = [NSString stringWithUTF8String:profilepic];
                NSString * accessToken=[NSString stringWithUTF8String:accesstoken];
                
                NSMutableDictionary * temp=[[NSMutableDictionary alloc]init];
                [temp setObject:userId forKey:@"userId"];
                [temp setObject:userFullName forKey:@"userFullName"];
                [temp setObject:profilePic forKey:@"profilePic"];
                [temp setObject:accessToken forKey:@"accessToken"];
                
                [SingletonClass shareSinglton].profileImgStr=profilePic;
                [[SingletonClass shareSinglton].allData addObject:temp];
            }
            
        }
        sqlite3_finalize(compiledStmt);
    }
    sqlite3_close(database);
    [[NSNotificationCenter defaultCenter]postNotificationName:@"reloadData" object:nil];
}



#pragma mark- delete data from sqlite

-(void)deleteSelectedUserFromDB{
   
    
    [activityIndicator startAnimating];
    NSArray * paths=NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSString * documtntPath=[paths objectAtIndex:0];
    NSString * databsePath=[documtntPath stringByAppendingPathComponent:@"board11.sqlite"];
    if(sqlite3_open([databsePath UTF8String], &database) == SQLITE_OK)
    {
        NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
        
        NSString * query= [NSString stringWithFormat:@"DELETE FROM InstaBoard where AccessToken=\"%@\"",accessToken];
        
        const char *sql =[query UTF8String];
        sqlite3_stmt *statement;
        if(sqlite3_prepare_v2(database, sql,-1, &statement, NULL) == SQLITE_OK)
        {
            if(sqlite3_step(statement) == SQLITE_DONE){
                
            }else{
                
            }
        }
        sqlite3_finalize(statement);
    }
    sqlite3_close(database);
}




-(void)cancelButtonAction:(UIButton*)sender{
    [self dismissViewControllerAnimated:YES completion:nil];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
