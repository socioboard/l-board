//
//  ShareViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ShareViewController : UIViewController<UITextViewDelegate>
{
    UITextView *shareTextView;
    CGSize windowSize;
}
@end
