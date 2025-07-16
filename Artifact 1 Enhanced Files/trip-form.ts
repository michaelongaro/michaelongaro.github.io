import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TripDataService } from '../services/trip-data';
import { S3UploadService } from '../services/s3-upload';

@Component({
  selector: 'app-trip-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './trip-form.html',
  styleUrl: './trip-form.css',
})
export class TripFormComponent implements OnInit {
  tripForm!: FormGroup;
  mode: 'add' | 'edit' = 'add';
  submitted = false;
  message: string = '';
  selectedFile: File | null = null;
  uploadProgress: number = 0;
  isUploading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private tripService: TripDataService,
    private s3UploadService: S3UploadService
  ) {}

  ngOnInit() {
    // Determine if we're in add or edit mode
    this.route.data.subscribe((data) => {
      if (data['mode']) {
        this.mode = data['mode'];
      }
    });

    this.initForm();

    if (this.mode === 'edit') {
      this.loadTripData();
    }
  }

  private initForm() {
    this.tripForm = this.formBuilder.group({
      _id: [],
      code: ['', Validators.required],
      name: ['', Validators.required],
      length: ['', Validators.required],
      start: ['', Validators.required],
      resort: ['', Validators.required],
      perPerson: ['', Validators.required],
      image: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  private loadTripData() {
    // Retrieve stashed trip ID
    let tripCode = localStorage.getItem('tripCode');
    if (!tripCode) {
      alert("Something wrong, couldn't find where I stashed tripCode!");
      this.router.navigate(['']);
      return;
    }

    this.tripService.getTrip(tripCode).subscribe({
      next: (value: any) => {
        if (!value || value.length === 0) {
          this.message = 'No Trip Retrieved!';
        } else {
          this.message = 'Trip: ' + tripCode + ' retrieved';
          // Populate our record into the form
          this.tripForm.patchValue(value[0]);
        }
      },
      error: (error: any) => {
        console.log('Error: ' + error);
      },
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  async uploadImage(): Promise<string> {
    if (!this.selectedFile) {
      // If no new file is selected, return the existing image URL
      return this.tripForm.get('image')?.value || '';
    }

    this.isUploading = true;
    try {
      const uploadResult = await this.s3UploadService.uploadFile(
        this.selectedFile,
        (progress) => {
          this.uploadProgress = progress;
        }
      );
      this.isUploading = false;
      return uploadResult.imageUrl;
    } catch (error) {
      console.error('Error uploading image:', error);
      this.isUploading = false;
      throw error;
    }
  }

  async onSubmit() {
    this.submitted = true;

    if (this.tripForm.valid) {
      try {
        // If a file is selected, upload it first
        if (this.selectedFile) {
          const imageUrl = await this.uploadImage();
          this.tripForm.patchValue({ image: imageUrl });
        }

        // Based on mode, call the appropriate service method
        if (this.mode === 'add') {
          this.tripService.addTrip(this.tripForm.value).subscribe({
            next: (data: any) => {
              console.log(data);
              this.router.navigate(['']);
            },
            error: (error: any) => {
              console.log('Error: ' + error);
            },
          });
        } else {
          this.tripService.updateTrip(this.tripForm.value).subscribe({
            next: (value: any) => {
              console.log(value);
              this.router.navigate(['']);
            },
            error: (error: any) => {
              console.log('Error: ' + error);
            },
          });
        }
      } catch (error) {
        console.error('Error during form submission:', error);
      }
    }
  }

  // get the form short name to access the form fields
  get f() {
    return this.tripForm.controls;
  }
}
