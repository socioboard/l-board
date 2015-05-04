//
//  ShareViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "ShareViewController.h"

#import "AppDelegate.h"
@interface ShareViewController ()

@end

@implementation ShareViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    windowSize=[UIScreen mainScreen].bounds.size;
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(createUI) name:@"loadShare" object:nil];
    
    // Do any additional setup after loading the view from its nib.
}


-(void)createUI{
    if (shareTextView) {
        shareTextView=nil;
    }
    shareTextView=[[UITextView alloc]initWithFrame:CGRectMake(20, 20, windowSize.width-40, 150)];
    shareTextView.layer.borderColor=[UIColor lightGrayColor].CGColor;
    shareTextView.layer.borderWidth=0.7;
    shareTextView.layer.cornerRadius=5;
    shareTextView.clipsToBounds=YES;
    shareTextView.delegate=self;
    shareTextView.text=@"Eneter your caption here.";
    [self.view addSubview:shareTextView];
    
    
    UIButton * share=[UIButton buttonWithType:UIButtonTypeCustom];
    share.frame=CGRectMake(windowSize.width-110, 200, 85, 25);
    [share setBackgroundImage:[UIImage imageNamed:@"sharet.png"] forState:UIControlStateNormal];
    share.layer.cornerRadius=5;
    share.clipsToBounds=YES;
    [share addTarget:self action:@selector(shareAction) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:share];
}
-(BOOL)textViewShouldBeginEditing:(UITextView *)textView{
    shareTextView.text=@"";
    return YES;
}

// share action to share Text on user wall.
-(void)shareAction{
  
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSString * accessToken= [[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSURL * postUrl=[NSURL URLWithString:[NSString stringWithFormat:@"https://api.linkedin.com/v1/people/~/shares?format=json&oauth2_access_token=%@",accessToken]];
    
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:postUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"POST"];
    
    NSDictionary *update = [[NSDictionary alloc] initWithObjectsAndKeys:
                            [[NSDictionary alloc]
                             initWithObjectsAndKeys:
                             @"anyone",@"code",nil], @"visibility",
                            shareTextView.text, @"comment", nil];
    NSLog(@"update %@",update);
    
    NSData * data1= [[self convertingIntoJsonString:update] dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
    
    
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    
    [request setValue:@"x-li-format" forHTTPHeaderField:@"json"];
    
  
    
    
    [request setHTTPBody: data1];
    
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
    NSLog(@" response data of share %@",response);
    if ([response objectForKey:@"updateKey"]) {
        shareTextView.text=@"Eneter your caption here.";
        [[AppDelegate sharedAppDelegate]showToastMessage:@"Shared successfully"];
    }
    
}


-(NSString*)convertingIntoJsonString:(NSDictionary*)dict
{
    
    NSError *error = nil;
    NSData *json;
    NSString *jsonString;
    // Dictionary convertable to JSON ?
    if ([NSJSONSerialization isValidJSONObject:dict])
    {
        // Serialize the dictionary
        json = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
        
        // If no errors, let's view the JSON
        if (json != nil && error == nil)
        {
            jsonString = [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding];
            jsonString=[NSString stringWithFormat:@"%@\r",jsonString];
            NSLog(@"JSON: %@", jsonString);
        }
    }
    return jsonString;
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
