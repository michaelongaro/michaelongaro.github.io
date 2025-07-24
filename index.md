# Michael Ongaro's ePortfolio | CS-499 | SNHU

### Professional Summary

Not yet completed.

### Code Review

In this video, I walk through a detailed review of my project’s codebases, highlighting key areas for improvement and outlining the new features I plan to implement. For each major category: Software Engineering and Design, Algorithms and Data Structures, and Databases, I introduce specific problems that need to be addressed, from fixing bugs and optimizing existing logic to enhancing user experience and adding new capabilities. This review not only demonstrates my ability to critically analyze and improve my own work, but also sets the stage for the next steps in my development process.

[![Michael Ongaro CS-499 Code Review(s)](https://img.youtube.com/vi/xMXppFz7a4g/maxresdefault.jpg)](https://www.youtube.com/watch?v=xMXppFz7a4g)

### Artifacts

<details open>
<summary><b>Software Engineering and Design</b></summary>

<p>
I selected my full-stack travel application "Travlr" as the artifact for my enhancement in the software engineering and design category. This application was originally developed during my CS-465 course and consisted of a client-facing website built with Express and Handlebars, along with an admin portal built with Angular that interfaced with a MongoDB database. The original implementation stored all trip images locally within the project's source code, which created significant limitations in terms of scalability, deployment, and maintenance. This design choice was functional for the original course requirements but did not align with industry best practices for handling static assets in a production environment.
</p>

<p>
I chose this artifact because it provided an excellent opportunity to demonstrate my understanding of modern software architecture principles, particularly the decoupling of static assets from application servers. The enhancement process involved integrating AWS S3 for cloud-based image storage and creating a reusable Angular component that consolidated the previously separate add and edit trip functionality. This implementation showcased my ability to design efficient, reusable components following the DRY (Don't Repeat Yourself) principle, as well as my skills in integrating third-party cloud services into existing applications. By refactoring the codebase to use a single component with conditional logic based on the current mode (add or edit), I demonstrated an understanding of component-based architecture that improves maintainability while reducing code duplication. I also added progress tracking for the image uploads which significantly improved the user experience compared to the original implementation.
</p>

<p>
Through this enhancement process, I successfully met course outcomes related to software engineering and design by implementing industry-standard practices for cloud integration and component reusability. I learned valuable lessons about the complexities involved in modifying existing systems, particularly when integrating new technologies and refactoring to improve architecture. The process required some more extensive modifications than I initially planned for, as modularizing the form components required changes across multiple files in the Angular codebase. One significant challenge I encountered involved configuring proper S3 bucket permissions and CORS policies, which required careful debugging using browser developer tools to identify and resolve issues with cross-origin requests. This experience strengthened my security mindset by emphasizing the importance of properly configured access controls when integrating external services, directly supporting the course outcome related to anticipating potential vulnerabilities and ensuring data security.
</p>

<p><b>Original Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalAddTrip.png" alt="Original Add Trip" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Original%20Screenshots/OriginalEditTrip.png" alt="Original Edit Trip" />

<p><b>Enhanced Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewAddTrip.png" alt="Enhanced Add Trip" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%201%20Enhanced%20Screenshots/NewEditTrip.png" alt="Enhanced Edit Trip" />

<p><b>Files</b></p>
<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Original%20Files">Artifact 1 Original Files</a></li>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%201%20Enhanced%20Files">Artifact 1 Enhanced Files</a></li>
</ul>

</details>

<details>
<summary><b>Algorithms and Data Structures</b></summary>

<p>I selected my Android inventory management application "Inventory Pro" as the artifact for my enhancement in the algorithms and data structure category. This application was originally developed during my CS-360 course and provided basic functionality for users to add, edit, and delete inventory items stored in a local SQLite database. The original implementation displayed inventory items in a simple, unsorted list based on the order they were retrieved from the database, which created significant usability issues as the inventory size grew. Users had no efficient way to locate specific items or analyze their inventory data, making the application impractical for real-world use cases where quick access to organized information is essential.</p>
<p>I chose this artifact because it provided an excellent opportunity to demonstrate my understanding of fundamental algorithmic principles and their practical application in solving real-world problems. The enhancement process involved implementing an efficient sorting system using the Merge Sort algorithm, which guaranteed O(n log n) performance regardless of dataset size. This implementation showcased my ability to analyze algorithmic complexity and select appropriate solutions that balance performance with scalability requirements. I created a comprehensive sorting framework that included multiple comparison criteria (item name, quantity, and creation date), bidirectional sorting options, and a generic utility class that could be extended for future enhancements. The solution required adding a new createdAt field to the database schema, implementing custom comparator classes following the Strategy design pattern, and creating an intuitive user interface with dropdown controls that allowed users to easily change sorting preferences.</p>
<p>I successfully met course outcomes related to designing and evaluating computing solutions using algorithmic principles and computer science best practices. The implementation demonstrated my ability to use well-founded techniques to deliver tangible value by transforming an inefficient, unsorted list into a highly organized and user-friendly interface. I learned valuable lessons about the importance of choosing appropriate algorithms for specific use cases, particularly how Merge Sort's stable sorting behavior and guaranteed performance made it superior to simpler alternatives like Bubble Sort or Selection Sort for this application. The process required more extensive database modifications than I initially realized, as I needed to implement proper schema migration to handle existing inventory items that lacked creation timestamps while ensuring backward compatibility.</p>
<p>One significant challenge I encountered involved refamiliarizing myself with the recursive nature of merge sort implementation and ensuring the algorithm worked correctly with custom comparator objects. I had to carefully debug the merging logic to handle edge cases where inventory items had null or empty values in their comparison fields. Another complexity came up during database schema modification, where I needed to design an upgrade strategy that would add the new createdAt column to existing installations without losing user data. This experience strengthened my understanding of both theoretical algorithmic concepts and practical software engineering considerations, particularly the importance of defensive programming when handling user data and the complexities involved in evolving database schemas in deployed applications.</p>


<p><b>Original Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Original%20Screenshots/MainMenu.png" alt="Main page" />

<p><b>Enhanced Screenshots</b></p>

<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Enhanced%20Screenshots/MainPage.png" alt="Main page" />
<img src="https://raw.githubusercontent.com/michaelongaro/michaelongaro.github.io/refs/heads/main/Artifact%202%20Enhanced%20Screenshots/SortingMenuOpen.png" alt="Sorting menu open" />

<p><b>Files</b></p>
<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%202%20Original%20Files">Artifact 2 Original Files</a></li>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%202%20Enhanced%20Files">Artifact 2 Enhanced Files</a></li>
</ul>

</details>

<details>
<summary><b>Databases</b></summary>

<ul>
  <li><a href="https://github.com/michaelongaro/michaelongaro.github.io/tree/main/Artifact%203%20Original%20Files">Artifact 3 Original Files</a></li>
</ul>

</details>
