# UniPath

## Description
The purpose of this app is to assist students and parents with finding and applying to colleges. 

## Core user stories
- [X] See list of colleges
- [X] See list of colleges saved to favorites
- [X] See detailed college information
- [X] Calendar that shows deadlines taken from list of favorites (color code?)
- [X] When user favorites a college, deadlines for that college get added to a calendar
- [ ] Financial aid: (e.g estimated cost, FAFSA, waiver fees)
- [ ] Push notifications - Reminders for deadlines
- [ ] Notification center
- Account settings:
  - [ ] how often notified
  - [ ] how many days before deadline
  - [ ] default settings
- Search/Filtering:
  - [X] name of college
  - [ ] cost of college
  - [ ] size of college you're looking for
  - [ ] location
  - [ ] acceptance rate
  - [ ] major
  - [ ] average GPA/SAT
  - [ ] happiness of students
- [X] Map of saved colleges
- [ ] notify if finished applications

## Optional user stores
- [ ] Log in as either college admissions representative or general
- [ ] Map based search (List view vs Map view)
- [ ] Showing colleges on map based on criteria (places that offer testing
- [ ] Financial aid calculator that is specific to each college
- [ ] Push notifications - Reminders to parents for deadlines (text message/email)
- [ ] Suggestions to succeed while in high school? (AP classes, leadership opportunities, volunteering)
- [ ] More detailed information about campus life/majors/classes (link to website?)
- [ ] feed page/reviews
- [ ] after accepted what to do

## Goals for this week
- Search/Filtering:
  - [ ] Cost, Size (# of students), Acceptance Rate, average GPA/SAT
  - [ ] Location, looking on a map
  - [ ] Majors and maybe happiness?
- [ ] Get notifications done
- Deadlines:
  - [ ] Implement financial aid deadlines? (give special icons $)
  - [ ] Implement custom deadlines
- Map:
  - [ ] Long click on pin favorites college (add animation to let user know)
  - [ ] Short click on pin opens a detailed activity about college
  - [ ] pin decoration/message over pin should be image of college? (Maybe: first click = name, second click = image)
  - [ ] adjust activity such that it takes in a list of colleges from respective fragments/activities map is used in
  - [ ] hide/remove actionbar
- College Details:
  - [ ] Research implementing a financial aid calculator (add a tab for it?)
  - [ ] Add maps pin/button to allow user to see where on map college is
  - [ ] Change icon colors in bottom navigation to be more visible. Inactive is black, change to white or transparent white.
  - [ ] OPTIONAL: turn details activity into a scroll view? research how it would look. (Lyft uses scroll view btw)
- Calendar:
  - [ ] Make calendar look prettier?
  - [ ] Add buttons on action bar or on view that takes you to current day and takes you to closest deadline.
  - [ ] Upon refresh, calendar should load deadlines user's date is currently on without having to click the date again.
  - [ ] OPTIONAL: change colors of events when they are "completed", "incomplete", or "active".
- Minor Improvements:
  - [ ] Search refresh should refresh the filtered list, not the entire list.
  - [ ] Add more colleges/more data to database
  - [ ] Time based refreshing for progress bar OR...
  - [ ] Upon scrolling up event listener, refresh progress bar and favorite colleges.
  - [ ] Arrow images in profile should scroll favorite colleges
  - [ ] Link to Website in details
  - [ ] If there are no search results, then add a message "No colleges" or something similar on view.
  - [ ] If there are no deadlines for a day in calendar fragment, add message "no deadlines today" or something similar in view.
  - [ ] Change action bar such that it either displays an icon, or displays words in the middle. Or have search icon be clickable over entire action bar.
  - [X] LOCK PORTRAIT MODE
  - [ ] Remove unnecessary toasts. (e.g map location changes, firebase database additions and removals, etc.)
  - [ ] Add actual launcher icon
  - [ ] OPTIONAL: begin adding pretty animations
  - [ ] OPTIONAL: begin researching optional features

## Walkthrough week 2 progress
![Walkthrough](walkthrough_week_2.mp4)
