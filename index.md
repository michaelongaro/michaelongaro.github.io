# Michael Ongaro's ePortfolio | CS-499 | SNHU

### Professional Summary

Not yet completed.

### Code Review

In this video, I walk through a detailed review of my project’s codebases, highlighting key areas for improvement and outlining the new features I plan to implement. For each major category: Software Engineering and Design, Algorithms and Data Structures, and Databases, I introduce specific problems that need to be addressed, from fixing bugs and optimizing existing logic to enhancing user experience and adding new capabilities. This review not only demonstrates my ability to critically analyze and improve my own work, but also sets the stage for the next steps in my development process.

[![Michael Ongaro CS-499 Code Review(s)](https://img.youtube.com/vi/xMXppFz7a4g/maxresdefault.jpg)](https://www.youtube.com/watch?v=xMXppFz7a4g)

### Artifacts

<details open>
<summary><b>Software Engineering and Design</b></summary>

I selected my full-stack travel application "Travlr" as the artifact for my enhancement in the software engineering and design category. This application was originally developed during my CS-465 course and consisted of a client-facing website built with Express and Handlebars, along with an admin portal built with Angular that interfaced with a MongoDB database. The original implementation stored all trip images locally within the project's source code, which created significant limitations in terms of scalability, deployment, and maintenance. This design choice was functional for the original course requirements but did not align with industry best practices for handling static assets in a production environment.

I chose this artifact because it provided an excellent opportunity to demonstrate my understanding of modern software architecture principles, particularly the decoupling of static assets from application servers. The enhancement process involved integrating AWS S3 for cloud-based image storage and creating a reusable Angular component that consolidated the previously separate add and edit trip functionality. This implementation showcased my ability to design efficient, reusable components following the DRY (Don't Repeat Yourself) principle, as well as my skills in integrating third-party cloud services into existing applications. By refactoring the codebase to use a single component with conditional logic based on the current mode (add or edit), I demonstrated an understanding of component-based architecture that improves maintainability while reducing code duplication. I also added progress tracking for the image uploads which significantly improved the user experience compared to the original implementation.

Through this enhancement process, I successfully met course outcomes related to software engineering and design by implementing industry-standard practices for cloud integration and component reusability. I learned valuable lessons about the complexities involved in modifying existing systems, particularly when integrating new technologies and refactoring to improve architecture. The process required some more extensive modifications than I initially planned for, as modularizing the form components required changes across multiple files in the Angular codebase. One significant challenge I encountered involved configuring proper S3 bucket permissions and CORS policies, which required careful debugging using browser developer tools to identify and resolve issues with cross-origin requests. This experience strengthened my security mindset by emphasizing the importance of properly configured access controls when integrating external services, directly supporting the course outcome related to anticipating potential vulnerabilities and ensuring data security.

##### Original Screenshots

![Original Add Trip](https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalAddTrip.png)
![Original Edit Trip](https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalEditTrip.png)

##### Enhanced Screenshots

![Enhanced Add Trip](https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewAddTrip.png)
![Enhanced Edit Trip](https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewEditTrip.png)

##### Files

- [Artifact 1 Original Files](https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Original%20Files)
- [Artifact 1 Enhanced Files](https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Enhanced%20Files)

</details>

<details>
<summary><b>Algorithms and Data Structures</b></summary>

- [Artifact 2 Original Files](https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%202%20Original%20Files)

</details>

<details>
<summary><b>Databases</b></summary>

- [Artifact 3 Original Files](https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%203%20Original%20Files)

</details>
